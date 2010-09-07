package ch.yvu.handyzahlung;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

public class SMSSender {

	private Context context;
	
	public SMSSender(Context context)
	{
		this.context = context;
	}
		
	public void sendSaldoSMS()
	{
		sendSMS("SALDO");
	}
	
	public void sendKontobewegungenSMS()
	{
		sendSMS("BEWEGUNGEN");
	}
	
	public void sendZahlungSMS(CharSequence strWaehrung, CharSequence strBetrag, CharSequence strEmpfaenger, CharSequence strMitteilung)
	{
		String strText = "ZAHLE " + strWaehrung +  " " + strBetrag + " AN " + strEmpfaenger + " " + strMitteilung;
		sendSMS(strText);
	}
	
	private void sendSMS(String strText)
	{
		//Nummer bestimmen
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.context);
		String strNumber = settings.getString("SMSNumberPostfinance", "474");
		
		if(settings.getBoolean("SendSMS", true))
		{
			//SMS senden
			SmsManager sm = SmsManager.getDefault();
			sm.sendTextMessage(strNumber, null, strText, null, null);
		}
	}
}
