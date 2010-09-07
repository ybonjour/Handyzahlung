package ch.yvu.handyzahlung;

import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ZahlungStatusListAdapter extends CursorAdapter{

	public ZahlungStatusListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		updateView(view, context, cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
    	final LinearLayout listItem = (LinearLayout) inflater.inflate(android.R.layout.two_line_list_item, parent, false);
    	
    	updateView(listItem, context, cursor);
    	
    	return listItem;
	}
	
	private void updateView(View listItem, Context context, Cursor cursor)
	{
		//Empfänger setzen
    	int idxEmpfaenger = cursor.getColumnIndex(Zahlung.EMPFAENGER);
    	int idxBetrag = cursor.getColumnIndex(Zahlung.BETRAG);
    	TextView view1 = ((TextView) listItem.findViewById(android.R.id.text1));
    	view1.setText(context.getResources().getString(R.string.Zahlung) + ": " + cursor.getString(idxEmpfaenger) + ", " + cursor.getString(idxBetrag));
    	
    	//Beschreibung setzen
    	int idxStatus = cursor.getColumnIndex(Zahlung.STATUS);
    	int status = cursor.getInt(idxStatus);
    	
    	String strStatus;
    	String strColor;
    	switch(status)
    	{
    	case Zahlung.STATUS_ERFOLGREICH:
    		strStatus = context.getResources().getString(R.string.Erfolgreich);
    		strColor = "#8CD186";
    		break;
    	case Zahlung.STATUS_FEHLERHAFT:
    		strStatus = context.getResources().getString(R.string.Fehlerhaft);
    		strColor = "#FF7575";
    		break;
    	default:
    		strStatus = context.getResources().getString(R.string.Unbekannt);
    		strColor = "#FFFEC4";
    	}
    	
    	listItem.setBackgroundColor(Color.parseColor(strColor));
    	TextView view2 = ((TextView) listItem.findViewById(android.R.id.text2));
    	view2.setText(context.getResources().getString(R.string.Status) + ": " + strStatus);
    	
	}
}