package ch.yvu.handyzahlung;

import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;
import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListeZahlungen extends ListActivity {

	public static final int CONTEXTMENU_DETAILS = 1;
	public static final int CONTEXTMENU_LOESCHEN = 2;
	
	public static final int MENU_ALLEZAHLUNGENLOESCHEN = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setZahlungenListAdapter();
		getListView().setTextFilterEnabled(true);
		
		registerForContextMenu(getListView());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0, MENU_ALLEZAHLUNGENLOESCHEN, 0, R.string.AlleZahlungenLoeschen);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case MENU_ALLEZAHLUNGENLOESCHEN:
			getContentResolver().delete(Zahlung.CONTENT_URI, null, null);
			setZahlungenListAdapter();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle("Zahlung");
		menu.add(0, CONTEXTMENU_DETAILS, 0, R.string.Details);
		menu.add(0, CONTEXTMENU_LOESCHEN, 0, R.string.Loeschen);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId())
		{
		case CONTEXTMENU_DETAILS:
			showDetails(info.id);
			return true;
		case CONTEXTMENU_LOESCHEN:
			Uri uri = Uri.withAppendedPath(Zahlung.CONTENT_URI, String.valueOf(info.id));
			getContentResolver().delete(uri, null, null);
			setZahlungenListAdapter();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void showDetails(long id) {
		
		Uri uri = Uri.withAppendedPath(Zahlung.CONTENT_URI, String.valueOf(id));
		
		//Dialog zum Bearbeiten der Beschreibung anzeigen
		ZahlungDetailDialog dialog = new ZahlungDetailDialog(this, uri);
		
		dialog.show();
		
	}

	private void setZahlungenListAdapter()
	{
		String[] projection = new String[]{Zahlung._ID, Zahlung.EMPFAENGER, Zahlung.BETRAG, Zahlung.MITTEILUNG, Zahlung.STATUS, Zahlung.DATUM};
		Cursor cursor = managedQuery(Zahlung.CONTENT_URI, projection, null, null, null);
			
		ZahlungenListAdapter adapter = new ZahlungenListAdapter(this, cursor);
		setListAdapter(adapter);
	}
}