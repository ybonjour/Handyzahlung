package ch.yvu.handyzahlung;

import java.util.Date;
import ch.yvu.handyzahlung.provider.Handyzahlung.Empfaenger;
import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class HandyzahlungErfassen extends Activity{
	private static final int MENU_EINSTELLUNGEN = 1;
	private static final int MENU_EMPFAENGERANZEIGEN = 2;
	private static final int MENU_ZAHLUNGENANZEIGEN = 3;
	
	//Request-Codes
	private static final int REQUEST_PICKCONTACT = 1;
	private static final int REQUEST_PICKEMPFAENGER = 2;
	
	private Button mBezahlenButton;
	private Button mKontaktButton;
	private AutoCompleteTextView mEmpfaenger;
	private EditText mBetrag;
	private EditText mMitteilung;
	private Spinner mWaehrung;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handyzahlung_erfassen);

        //Daten für Währungen laden
        mWaehrung = (Spinner) findViewById(R.id.Waehrung);
        ArrayAdapter<CharSequence> adapterWaehrung = ArrayAdapter.createFromResource(this, R.array.Waehrungen, android.R.layout.simple_spinner_item);
        adapterWaehrung.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWaehrung.setAdapter(adapterWaehrung);
                
        //onClickListener Kontakt-Button
        mKontaktButton = (Button) findViewById(R.id.Kontakte);
        mKontaktButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PickContact();
			}
		});
              
        //onClickListener ZahlungAusloesen-Button
        mBezahlenButton = (Button) findViewById(R.id.Bezahlen);
        mBezahlenButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				sendSMS();
			}
		});
        
        setEmpfaengerAutoCompleteAdapter();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_EINSTELLUNGEN, 0, R.string.Einstellungen).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_EMPFAENGERANZEIGEN, 0, R.string.EmpfaengerAnzeigen);
		menu.add(0, MENU_ZAHLUNGENANZEIGEN, 0, R.string.ZahlungenAnzeigen);
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case MENU_EINSTELLUNGEN:
			Intent settingsActivity = new Intent(getBaseContext(), Preferences.class);
			startActivity(settingsActivity);
			return true;
		case MENU_EMPFAENGERANZEIGEN:
			Intent empfaengerActivity = new Intent(getBaseContext(), ListeEmpfaenger.class);
			startActivityForResult(empfaengerActivity, REQUEST_PICKEMPFAENGER);
			return true;
		case MENU_ZAHLUNGENANZEIGEN:
			Intent zahlungActivity = new Intent(getBaseContext(), ListeZahlungen.class);
			startActivity(zahlungActivity);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{	
		switch(requestCode)
		{
		case REQUEST_PICKCONTACT:
			if(resultCode != RESULT_CANCELED) HandleContactPicked(data);
			return;
		case REQUEST_PICKEMPFAENGER:
			if(resultCode == RESULT_OK) HandleEmpfaengerPicked(data);
			setEmpfaengerAutoCompleteAdapter();
			return;
		}	
	}

	private void sendSMS()
	{
		mEmpfaenger = (AutoCompleteTextView) findViewById(R.id.Empfaenger);
		CharSequence strEmpfaenger = mEmpfaenger.getText();
		
		mBetrag = (EditText) findViewById(R.id.Betrag);
		CharSequence strBetrag = mBetrag.getText();
		
		mMitteilung = (EditText) findViewById(R.id.Mitteilung);
		CharSequence strMitteilung = mMitteilung.getText();
		
		mWaehrung = (Spinner) findViewById(R.id.Waehrung);
		CharSequence strWaehrung = mWaehrung.getSelectedItem().toString();
		
		if(mEmpfaenger.getText().length() == 0 || mBetrag.getText().length() == 0)
		{
			Toast errorToast = Toast.makeText(this, R.string.NichtAlleAngaben, Toast.LENGTH_LONG);
			errorToast.show();
			return;
		}
		
		String strText = "ZAHLE " + strWaehrung +  " " + strBetrag + " AN " + strEmpfaenger + " " + strMitteilung;  
		
		//Nummer bestimmen
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String strNumber = settings.getString("SMSNumberPostfinance", "474");
				
		if(settings.getBoolean("SendSMS", false))
		{
			//SMS senden
			SmsManager sm = SmsManager.getDefault();
			sm.sendTextMessage(strNumber, null, strText, null, null);
		}
		
		//Zahlung speichern
		saveZahlung(strEmpfaenger.toString(), strWaehrung + " " + strBetrag, strMitteilung.toString());
				
		//Info Toast anzeigen
		Toast toast = Toast.makeText(getApplicationContext(), strNumber + ":" + strText, Toast.LENGTH_LONG);
		toast.show();
	}
	
	private void saveZahlung(String strEmpfaenger, String strBetrag, String strMitteilung)
	{
		int empfaengerId = getOrCreateEmpfaenger(strEmpfaenger);
		
		ContentValues values = new ContentValues();
		values.put(Zahlung.EMPFAENGER, empfaengerId);
		values.put(Zahlung.BETRAG, strBetrag);
		values.put(Zahlung.MITTEILUNG, strMitteilung);
		values.put(Zahlung.STATUS, Zahlung.STATUS_UNBEKANNT);

		Date date = new Date();
		values.put(Zahlung.DATUM, date.getTime());
		getContentResolver().insert(Zahlung.CONTENT_URI, values);
	}
	
	private int getOrCreateEmpfaenger(String strEmpfaenger)
	{
		//Empfänger speichern, falls er nicht schon existiert
		String[] projection = new String[]{Empfaenger._ID, Empfaenger.EMPFAENGER};
		Cursor cursor = managedQuery(Empfaenger.CONTENT_URI, projection, Empfaenger.EMPFAENGER + "='" + strEmpfaenger+"'", null, null);
		if(cursor.getCount() != 0)
		{
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(Empfaenger._ID);
			return cursor.getInt(idx);
		}
		
		//Beschreibung falls möglich bestimmen
		String strBeschreibung = "";
		Uri uriContacts = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(strEmpfaenger));
		String[] projectionContacts = new String[] {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.DISPLAY_NAME};
		Cursor cursorContacts = managedQuery(uriContacts, projectionContacts, null, null, null);		
		if(cursorContacts.getCount() == 1)
		{
			cursor.moveToFirst();
			int idxDisplayName = cursorContacts.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			strBeschreibung = cursorContacts.getString(idxDisplayName);
		}
		
		//Empfänger anlegen
		ContentValues values = new ContentValues();
		values.put(Empfaenger.EMPFAENGER, strEmpfaenger.toString());
		values.put(Empfaenger.BESCHREIBUNG, strBeschreibung);
		Uri uriEmpfaenger = getContentResolver().insert(Empfaenger.CONTENT_URI, values);
		return Integer.parseInt(uriEmpfaenger.getPathSegments().get(1)); 
	}
	
	private void PickContact()
	{
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, REQUEST_PICKCONTACT); 
	}
	
	private void HandleEmpfaengerPicked(Intent data) {
		if(data == null) return;
		
		String[] projection = new String[]{Empfaenger._ID, Empfaenger.EMPFAENGER};
		Cursor cursor = managedQuery(data.getData(), projection, null, null, null);
		cursor.moveToFirst();
		
		int idxEmpfaenger = cursor.getColumnIndex(Empfaenger.EMPFAENGER);
		String strEmpfaenger = cursor.getString(idxEmpfaenger);
		setEmpfaenger(strEmpfaenger);
	}

	private void HandleContactPicked(Intent data) {
		if(data == null) return;
		
		Cursor c = getContact(data.getData());
		if (c.moveToFirst())
		{
			String strNumber = c.getString(c.getColumnIndexOrThrow(ContactsContract.PhoneLookup.NUMBER));
			setEmpfaenger(strNumber);
		}
	}

	private Cursor getContact(Uri uri)
    {
        // Run query
    	Cursor c =  managedQuery(uri, null, null, null, null);
    	return c; 
    }
    
    private void setEmpfaengerAutoCompleteAdapter()
    {
    	//AutoComplete für Empfänger
    	if(mEmpfaenger == null) mEmpfaenger = (AutoCompleteTextView) findViewById(R.id.Empfaenger);

    	String[] projection = new String[]{Empfaenger._ID, Empfaenger.EMPFAENGER, Empfaenger.BESCHREIBUNG};
    	Cursor cursor = managedQuery(Empfaenger.CONTENT_URI, projection, null, null, null);
    	
    	EmpfaengerListAdapter adapter = new EmpfaengerListAdapter(this, cursor);        
        mEmpfaenger.setAdapter(adapter);
    }
    
    private void setEmpfaenger(String strEmpfaenger)
    {
    	if(mEmpfaenger == null)	mEmpfaenger = (AutoCompleteTextView) findViewById(R.id.Empfaenger);
		mEmpfaenger.setText(strEmpfaenger);
    }
}
