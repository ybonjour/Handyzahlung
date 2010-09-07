package ch.yvu.handyzahlung;

import java.util.Date;

import ch.yvu.handyzahlung.provider.Handyzahlung.Kontobewegung;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class KontobewegungenAbfragen extends Activity {

	private Button mAktualisieren;
	private ListView mKontobewegungenList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.kontobewegungen_abfragen);
		
		//Kontobewegungen-Anzeige initialisiern
		mKontobewegungenList = (ListView) findViewById(R.id.KontobewegungenList);
		String[] projection = new String[]{Kontobewegung._ID, Kontobewegung.DATUM, Kontobewegung.TEXT};
		Cursor c = managedQuery(Kontobewegung.CONTENT_URI, projection, null, null, null);
		String[] fromColumns = new String[]{Kontobewegung.TEXT, Kontobewegung.DATUM};
		int[] toLayoutIDs = new int[]{android.R.id.text1, android.R.id.text2};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, c, fromColumns, toLayoutIDs);
		mKontobewegungenList.setAdapter(adapter);
		
		mAktualisieren = (Button) findViewById(R.id.KontobewegungenAktualisieren);
		mAktualisieren.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				sendSMS();
			}
		});
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean autoBewegungen = settings.getBoolean("AutoBewegungen", false);
		
		if(autoBewegungen && !isRefreshing()) sendSMS();
	}
	
	private void sendSMS()
	{
		SMSSender sender = new SMSSender(getApplicationContext());
		sender.sendKontobewegungenSMS();
		
		getContentResolver().delete(Kontobewegung.CONTENT_URI, null, null);
		
		ContentValues values = new ContentValues();
		Date date = new Date();
		values.put(Kontobewegung.DATUM, date.toLocaleString());
		values.put(Kontobewegung.TEXT, getString(R.string.KontobewegungenWerdenAktualisiert));
		getContentResolver().insert(Kontobewegung.CONTENT_URI, values);
	}
	
	private boolean isRefreshing()
	{
		String[] projection = new String[]{Kontobewegung._ID};
		Cursor c = managedQuery(Kontobewegung.CONTENT_URI, projection, Kontobewegung.TEXT + "='" + getString(R.string.KontobewegungenWerdenAktualisiert)+ "'", null, null);
		
		return c.getCount() > 0;
	}
}