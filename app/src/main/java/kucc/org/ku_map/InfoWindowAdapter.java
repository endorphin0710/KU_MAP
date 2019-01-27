package kucc.org.ku_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public InfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v =  inflater.inflate(R.layout.info_window, null);

        TextView title = (TextView)v.findViewById(R.id.textView_title);
        title.setText(marker.getTitle());
        TextView content = (TextView)v.findViewById(R.id.textView_content);
        content.setText(marker.getSnippet());

        return v;
    }
}
