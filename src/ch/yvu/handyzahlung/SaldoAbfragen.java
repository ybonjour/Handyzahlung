package ch.yvu.handyzahlung;

import java.util.Date;
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
import ch.yvu.handyzahlung.provider.Handyzahlung.Saldo;

public class SaldoAbfragen extends Activity {

	private Button mAktualisieren;
	private ListView mSaldoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.saldo_abfragen);
		
		//Saldo-Anzeige initialisiern
		mSaldoList = (ListView) findViewById(R.id.SaldoList);
		String[] projection = new String[]{Saldo._ID, Saldo.DATUM, Saldo.TEXT};
		Cursor c = managedQuery(Saldo.CONTENT_URI, projection, null, null, null);
		String[] fromColumns = new String[]{Saldo.TEXT, Saldo.DATUM};
		int[] toLayoutIDs = new int[]{android.R.id.text1, android.R.id.text2};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, c, fromColumns, toLayoutIDs);
		mSaldoList.setAdapter(adapter);
		
		mAktualisieren = (Button) findViewById(R.id.SaldoAktualisieren);
		mAktualisieren.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				sendSMS();
			}
		});
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean autoSaldo = settings.getBoolean("AutoSaldo", false);
		
		if(autoSaldo && !isRefreshing()) sendSMS();
	}
	
	private void sendSMS()
	{
		SMSSender sender = new SMSSender(getApplicationContext());
		sender.sendSaldoSMS();
		
		getContentResolver().delete(Saldo.CONTENT_URI, null, null);
		
		ContentValues values = new ContentValues();
		Date date = new Date();
		values.put(Saldo.DATUM, date.toLocaleString());
		values.put(Saldo.TEXT, getString(R.string.SaldoWirdAktualisiert));
		getContentResolver().insert(Saldo.CONTENT_URI, values);
	}
	
	private boolean isRefreshing()
	{
		String[] projection = new String[]{Saldo._ID};
		Cursor c = managedQuery(Saldo.CONTENT_URI, projection, Saldo.TEXT + "='" + getString(R.string.SaldoWirdAktualisiert) + "'", null, null);
		
		return c.getCount() > 0;
	}
}