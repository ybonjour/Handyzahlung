package ch.yvu.handyzahlung;

import java.util.Date;

import ch.yvu.handyzahlung.provider.Handyzahlung.Empfaenger;
import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HandyzahlungErfassen extends Activity{
	//Optionsmenu Code
	private static final int MENU_EMPFAENGERANZEIGEN = 1;
	
	//Request-Codes
	private static final int REQUEST_PICKCONTACT = 1;
	private static final int REQUEST_PICKEMPFAENGER = 2;
	
	private static final String SAVEKEY_ZAHLUNGSTATUSVISIBILITY = "ZahlungStatusVisibility";
	private static final String SAVEKEY_ZAHLUNGSTATUSURI = "ZahlungStatusUri";
	
	private Button mBezahlenButton;
	private Button mKontaktButton;
	private AutoCompleteTextView mEmpfaenger;
	private EditText mBetrag;
	private EditText mMitteilung;
	private Spinner mWaehrung;
	private ListView mStatusListe;
	
	private Uri statusUri;	
		
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
		menu.add(0, MENU_EMPFAENGERANZEIGEN, 0, R.string.EmpfaengerAnzeigen);
		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case MENU_EMPFAENGERANZEIGEN:
			Intent empfaengerActivity = new Intent(getBaseContext(), ListeEmpfaenger.class);
			startActivityForResult(empfaengerActivity, REQUEST_PICKEMPFAENGER);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(this.mStatusListe == null) this.mStatusListe = (ListView) findViewById(R.id.StatusList);
		int visibility = this.mStatusListe.getVisibility();
		outState.putInt(SAVEKEY_ZAHLUNGSTATUSVISIBILITY, visibility);
		
		if(this.statusUri != null)
		{
			outState.putString(SAVEKEY_ZAHLUNGSTATUSURI, this.statusUri.toString());
		}
	}
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if(this.mStatusListe == null) this.mStatusListe = (ListView) findViewById(R.id.StatusList);
		int visibility = savedInstanceState.getInt(SAVEKEY_ZAHLUNGSTATUSVISIBILITY);
		this.mStatusListe.setVisibility(visibility);
		
		if(savedInstanceState.containsKey(SAVEKEY_ZAHLUNGSTATUSURI))
		{
			Uri uri = Uri.parse(savedInstanceState.getString(SAVEKEY_ZAHLUNGSTATUSURI));
			setStautsListe(uri);
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
		
		//SMS senden
		SMSSender sender = new SMSSender(getApplicationContext());  
		sender.sendZahlungSMS(strWaehrung, strBetrag, strEmpfaenger, strMitteilung);

		//Zahlung speichern
		Uri uriZahlung = saveZahlung(strEmpfaenger.toString(), strWaehrung + " " + strBetrag, strMitteilung.toString());
		
		setStautsListe(uriZahlung);
	}
	
	private void setStautsListe(Uri uriZahlung) {
		Cursor cursor = managedQuery(uriZahlung, null, null, null, null);
		ZahlungStatusListAdapter adapter = new ZahlungStatusListAdapter(this, cursor);
		
		if(this.mStatusListe == null) this.mStatusListe = (ListView) findViewById(R.id.StatusList);
		this.mStatusListe.setAdapter(adapter);
		this.mStatusListe.setVisibility(View.VISIBLE);
		this.mStatusListe.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Uri uri = Uri.withAppendedPath(Zahlung.CONTENT_URI, String.valueOf(id));
				Cursor c = managedQuery(uri, null, null, null, null);
				if(!c.moveToFirst()) return;
				int status = c.getInt(c.getColumnIndex(Zahlung.STATUS));
				
				if(status != Zahlung.STATUS_ERFOLGREICH)
				{
					ZahlungDetailDialog dialog = new ZahlungDetailDialog(HandyzahlungErfassen.this, uri);
					dialog.show();					
				}
				
				if(status != Zahlung.STATUS_UNBEKANNT)
				{
					mStatusListe.setVisibility(View.GONE);
				}
			}
		});
		//Uri zwischenspeichern für Zustandsspeicherung
		this.statusUri = uriZahlung;
	}

	private Uri saveZahlung(String strEmpfaenger, String strBetrag, String strMitteilung)
	{
		int empfaengerId = getOrCreateEmpfaenger(strEmpfaenger);
		
		ContentValues values = new ContentValues();
		values.put(Zahlung.EMPFAENGER, empfaengerId);
		values.put(Zahlung.BETRAG, strBetrag);
		values.put(Zahlung.MITTEILUNG, strMitteilung);
		values.put(Zahlung.STATUS, Zahlung.STATUS_UNBEKANNT);

		Date date = new Date();
		values.put(Zahlung.DATUM, date.getTime());
		return getContentResolver().insert(Zahlung.CONTENT_URI, values);
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
		Cursor cPhone = managedQuery(Phone.CONTENT_URI, null, Phone.NUMBER + "= ? AND " + Phone.TYPE + "=" + Phone.TYPE_MOBILE , new String[]{strEmpfaenger}, null);
		if(cPhone.moveToFirst())
		{
			int idxName = cPhone.getColumnIndex(Phone.DISPLAY_NAME);
			strBeschreibung = cPhone.getString(idxName);
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
		
		String strNumber = getPhoneNumber(data.getData());
		setEmpfaenger(strNumber);
	}

	private String getPhoneNumber(Uri uriContact)
    {
        // Run query
    	Cursor cContact = managedQuery(uriContact, null, null, null, null);
    	if(!cContact.moveToFirst()) return "";
    	int idxId = cContact.getColumnIndex(ContactsContract.Contacts._ID);
    	String idContact = cContact.getString(idxId);
    	
    	Cursor cPhones = managedQuery(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=" + idContact + " AND " + Phone.TYPE + "=" + Phone.TYPE_MOBILE, null, null);
    	if(!cPhones.moveToFirst()) return "";
    	int idxNumber = cPhones.getColumnIndex(Phone.NUMBER);
    	String strNumber = cPhones.getString(idxNumber);
    	   	
    	return strNumber; 
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
