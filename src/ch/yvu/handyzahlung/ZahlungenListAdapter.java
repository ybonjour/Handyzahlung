package ch.yvu.handyzahlung;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;

public class ZahlungenListAdapter extends CursorAdapter{

	public ZahlungenListAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		updateView(view, context, cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
    	final LinearLayout listItem = (LinearLayout) inflater.inflate(R.layout.zahlung_item, parent, false);
    	
    	updateView(listItem, context, cursor);
    	
    	return listItem;
	}
	
	private void updateView(View listItem, Context context, Cursor cursor)
	{
		//SatusIcon bestimmen
    	int idxStatus = cursor.getColumnIndex(Zahlung.STATUS);
		int status = cursor.getInt(idxStatus);
				
		Drawable icon;
		switch(status)
		{
		case Zahlung.STATUS_ERFOLGREICH:
			icon = context.getResources().getDrawable(android.R.drawable.presence_online);
			break;
		case Zahlung.STATUS_FEHLERHAFT:
			icon = context.getResources().getDrawable(android.R.drawable.presence_offline);
			break;
		case Zahlung.STATUS_UNBEKANNT:
		default:
			icon = context.getResources().getDrawable(android.R.drawable.presence_invisible);
		}
		
		//StatusIcon setzen
		ImageView image = (ImageView) listItem.findViewById(R.id.ZahlungStatusIcon);
		image.setImageDrawable(icon);
		
		//Empfänger setzen
		int idxEmpfaenger = cursor.getColumnIndex(Zahlung.EMPFAENGER);
		TextView viewEmpfaenger = (TextView) listItem.findViewById(R.id.ZahlungEmpfaenger);
		viewEmpfaenger.setText(cursor.getString(idxEmpfaenger));
		
		//Betrag setzen
		int idxBetrag = cursor.getColumnIndex(Zahlung.BETRAG);
		TextView viewBetrag = (TextView) listItem.findViewById(R.id.ZahlungBetrag);
		viewBetrag.setText(cursor.getString(idxBetrag));		
		
		//Datum formatieren und setzen
		int idxDatum = cursor.getColumnIndex(Zahlung.DATUM);
		long timeStamp = cursor.getLong(idxDatum);
		Date datum = new Date(timeStamp);
		DateFormat df = DateFormat.getDateTimeInstance();
		TextView viewDatum = (TextView) listItem.findViewById(R.id.ZahlungDatum);
		viewDatum.setText(df.format(datum));
	}
}
