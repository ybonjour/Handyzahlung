package ch.yvu.handyzahlung;

import java.util.Date;

import ch.yvu.handyzahlung.provider.Handyzahlung.Zahlung;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class SMSReceiver extends BroadcastReceiver {
	public static final String KEY_BODY = "body";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if(bundle == null) return;

		Object[] pdus = (Object[]) bundle.get("pdus");
		SmsMessage[] msgs = new SmsMessage[pdus.length];

		for(int i=0; i < msgs.length; i++)
		{
			msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
			
			String strSender = msgs[i].getOriginatingAddress();
			
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			String strPostfinanceNumber = settings.getString("SMSNumberPostfinance", "474");
			
			if(!TextUtils.equals(strPostfinanceNumber, strSender)) continue;
			
			String strMessageBody = msgs[i].getMessageBody();
			
			//SMS f�r Gutschriften werden nicht ber�cksichtigt
			if(strMessageBody.startsWith("Gutschrift")) return;
						
			int status = Zahlung.STATUS_UNBEKANNT;
						
			if(strMessageBody.startsWith("Zahlung"))
			{
				status = Zahlung.STATUS_ERFOLGREICH;
			}
			else
			{
				status = Zahlung.STATUS_FEHLERHAFT;
			}
					
			//Der Status wird f�r die neuste Zahlung �bernommen
			//(Es werden nur Zahlungen, welche in den letzen 5 Minuten
			//verschickt wurden und noch den Status unbekannt haben ber�cksichtigt)
			Date dateReceived = new Date();
			Date dateFilterStart = new Date(dateReceived.getYear(), dateReceived.getMonth(), dateReceived.getDate(), dateReceived.getHours(), dateReceived.getMinutes()-5);
			String selection = Zahlung.DATUM + " > " + dateFilterStart.getTime();
			selection += " AND " + Zahlung.STATUS + "=" + Zahlung.STATUS_UNBEKANNT;
			
			Cursor c = context.getContentResolver().query(Zahlung.CONTENT_URI, new String[]{Zahlung._ID}, selection, null, Zahlung.DATUM + " DESC");
			if(c.getCount() == 0) return;
			c.moveToFirst();
			long id = c.getLong(0);
			Uri uri = Uri.withAppendedPath(Zahlung.CONTENT_URI, String.valueOf(id));
				
			setMessageStatus(context, uri, status, strMessageBody);
		}
	}
	
	private void setMessageStatus(Context context, Uri uri, int status, String statusText)
	{				
		ContentValues values = new ContentValues();
		values.put(Zahlung.STATUS, status);
		if(!TextUtils.isEmpty(statusText)) values.put(Zahlung.STATUSTEXT, statusText);
		
		context.getContentResolver().update(uri, values, null, null);			
		context.getContentResolver().notifyChange(uri, null);
	}
}
