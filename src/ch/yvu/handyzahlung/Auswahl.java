package ch.yvu.handyzahlung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Auswahl extends Activity {
	
	//Optionmenu Codes
	private static final int MENU_EINSTELLUNGEN = 1;
	
	private Button mZahlungErfassen;
	private Button mZahlungAnzeigen;
	private Button mSaldo;
	private Button mKontobewegungen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.auswahl);
		
		mZahlungErfassen = (Button) findViewById(R.id.ZahlungErfassen);
		mZahlungErfassen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startZahlungErfassen();
			}
		});
		
		mZahlungAnzeigen = (Button) findViewById(R.id.ZahlungAnzeigen);
		mZahlungAnzeigen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startZahlungenAnzeigen();
			}
		});
		
		mSaldo = (Button) findViewById(R.id.Saldo);
		mSaldo.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startSaldoAbfragen();
			}
		});
		
		mKontobewegungen = (Button) findViewById(R.id.Kontobewegungen);
		mKontobewegungen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				startKontobewegungenAbfragen();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_EINSTELLUNGEN, 0, R.string.Einstellungen).setIcon(android.R.drawable.ic_menu_preferences);
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
		}
		return false;
	}
	
	private void startZahlungErfassen(){
		Intent intentZahlungErfassen = new Intent(getBaseContext(), HandyzahlungErfassen.class);
		startActivity(intentZahlungErfassen);
	}
	
	private void startZahlungenAnzeigen() {
		Intent zahlungActivity = new Intent(getBaseContext(), ListeZahlungen.class);
		startActivity(zahlungActivity);
	}
	
	private void startSaldoAbfragen() {
		Intent saldoAcitivity = new Intent(getBaseContext(), SaldoAbfragen.class);
		startActivity(saldoAcitivity);
	}
	
	private void startKontobewegungenAbfragen() {
		Intent kontobewegungenActivity = new Intent(getBaseContext(), KontobewegungenAbfragen.class);
		startActivity(kontobewegungenActivity);		
	}
}
