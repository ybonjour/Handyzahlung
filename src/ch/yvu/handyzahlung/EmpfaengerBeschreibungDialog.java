package ch.yvu.handyzahlung;

import ch.yvu.handyzahlung.provider.Handyzahlung.Empfaenger;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EmpfaengerBeschreibungDialog extends Dialog{

	private Uri uri;
	private EditText mEmpfaengerBeschreibung;
		
	public EmpfaengerBeschreibungDialog(Context context, Uri uri) {
		super(context);
		this.uri = uri;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_empfaengerbeschreibung);
		
		setTitle(R.string.BeschreibungBearbeiten);
		//Beschreibung holen
		String[] projection = new String[]{Empfaenger._ID, Empfaenger.BESCHREIBUNG}; 
		Cursor cursor = getContext().getContentResolver().query(this.uri, projection, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(Empfaenger.BESCHREIBUNG);
		String strBeschreibung = cursor.getString(idx);
		
		//Beschreibung setzen
		mEmpfaengerBeschreibung = (EditText)findViewById(R.id.EmpfaengerBeschreibung);
		mEmpfaengerBeschreibung.setText(strBeschreibung);
		
		Button buttonOK = (Button) findViewById(R.id.ButtonOK);
		buttonOK.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				String strBeschreibung = mEmpfaengerBeschreibung.getText().toString();
				ContentValues values = new ContentValues();
				values.put(Empfaenger.BESCHREIBUNG, strBeschreibung);
				getContext().getContentResolver().update(uri, values, null, null);
				
				dismiss();
			}
		});
	}
}
