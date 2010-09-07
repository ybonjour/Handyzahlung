package ch.yvu.handyzahlung;

import ch.yvu.handyzahlung.provider.Handyzahlung.Empfaenger;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ListeEmpfaenger extends ListActivity{

	private static final int MENU_ALLEEMPFAENGERLOESCHEN = 1;
	private static final int CONTEXTMENU_UEBERNEHMEN = 1;
	private static final int CONTEXTMENU_BESCHREIBUNGBEARBEITEN = 2;
	private static final int CONTEXTMENU_LOESCHEN = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setEmpfaengerListAdapter();
		getListView().setTextFilterEnabled(true);
		
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_ALLEEMPFAENGERLOESCHEN, 0, R.string.AlleEmpfaengerLoeschen);		
		return true;		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case MENU_ALLEEMPFAENGERLOESCHEN:
			getContentResolver().delete(Empfaenger.CONTENT_URI, null, null);
			setEmpfaengerListAdapter();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle("Empfänger");
		
		menu.add(0, CONTEXTMENU_UEBERNEHMEN, 0, R.string.Uebernehmen);
		menu.add(0, CONTEXTMENU_BESCHREIBUNGBEARBEITEN, 0, R.string.BeschreibungBearbeiten);
		menu.add(0, CONTEXTMENU_LOESCHEN, 0, R.string.Loeschen);
	}
		
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId())
		{
		case CONTEXTMENU_LOESCHEN:
			Uri uri = Uri.withAppendedPath(Empfaenger.CONTENT_URI, String.valueOf(info.id));
			getContentResolver().delete(uri, null, null);
			setEmpfaengerListAdapter();
			return true;
		case CONTEXTMENU_UEBERNEHMEN:
			pickEmpfaenger(info.id);
			return true;
		case CONTEXTMENU_BESCHREIBUNGBEARBEITEN:
			setBeschreibung(info.id);
			setEmpfaengerListAdapter();
			return true;
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		pickEmpfaenger(id);
	}

	private void setEmpfaengerListAdapter()
	{
		String[] projection = new String[]{Empfaenger._ID, Empfaenger.EMPFAENGER, Empfaenger.BESCHREIBUNG};
		Cursor cursor = managedQuery(Empfaenger.CONTENT_URI, projection, null, null, null);
			
		String[] columns = new String[]{Empfaenger.EMPFAENGER, Empfaenger.BESCHREIBUNG};
		int[] views = new int[]{android.R.id.text1, android.R.id.text2};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, cursor, columns, views);
		setListAdapter(adapter);
	}
	
	private void pickEmpfaenger(long id)
	{
		Uri uri = Uri.withAppendedPath(Empfaenger.CONTENT_URI, String.valueOf(id));
		Intent data = new Intent();
		data.setData(uri);
		setResult(RESULT_OK, data);
		finish();
	}
	
	private void setBeschreibung(long id)
	{
		Uri uri = Uri.withAppendedPath(Empfaenger.CONTENT_URI, String.valueOf(id));
		
		//Dialog zum Bearbeiten der Beschreibung anzeigen
		EmpfaengerBeschreibungDialog dialog = new EmpfaengerBeschreibungDialog(this, uri);
		
		dialog.show();
	}
}
