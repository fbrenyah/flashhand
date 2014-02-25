package frankbrenyah.flashhand;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class Flash_Widget_Provider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        int N = appWidgetIds.length;
        for(int i=0; i<=N; ++i){
            int widgetId = appWidgetIds[i];

            Intent intentForReceiver = new Intent(context, Flash_Widget_Provider.class);
            intentForReceiver.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            intentForReceiver.setAction("widget_ACTION");
            intentForReceiver.setClass(context, Flash_Widget_Receiver.class);
            context.sendBroadcast(intentForReceiver);

            PendingIntent widgetPendingIntent = PendingIntent.getBroadcast(context, 0, intentForReceiver, 0);
            RemoteViews widgetViews = new RemoteViews(context.getPackageName(), R.layout.flashlight_widget);
            widgetViews.setOnClickPendingIntent(R.id.widgetButton, widgetPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, widgetViews);
        }
    }
}//end AppwidgetProvider
