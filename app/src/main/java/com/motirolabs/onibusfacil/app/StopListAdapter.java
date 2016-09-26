package com.motirolabs.onibusfacil.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StopListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "StopListAdapter";

    Activity activity;

    ArrayList<Stop> stops;

    public StopListAdapter(Activity activity, ArrayList<Stop> stops) {

        this.activity = activity;

        this.stops = stops;

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return stops.get(groupPosition).getRoutes().get(childPosition);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.route_item, parent, false);

            holder.tvRouteCode = (TextView) convertView.findViewById(R.id.tvRouteCode);
            holder.tvRouteName = (TextView) convertView.findViewById(R.id.tvRouteName);
            holder.tvRouteTime = (TextView) convertView.findViewById(R.id.tvRouteTime);

            convertView.setTag(holder);


        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        String routeCode = stops.get(groupPosition).getRoutes().get(childPosition).getCode();
        String routeName = stops.get(groupPosition).getRoutes().get(childPosition).getName();
        String routeTime = stops.get(groupPosition).getRoutes().get(childPosition).getTime();

        holder.tvRouteCode.setText(routeCode);
        holder.tvRouteName.setText(routeName);
        holder.tvRouteTime.setText(routeTime);

        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return stops.get(groupPosition).getRoutes().size();

    }

    @Override
    public Object getGroup(int groupPosition) {

        return stops.get(groupPosition);

    }

    @Override
    public int getGroupCount() {

        return stops.size();

    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;

    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.stop_item, viewGroup, false);

        }

        String stopName = stops.get(groupPosition).getAddress();
        String stopDistance = "Distancia: " + formatDistance(stops.get(groupPosition).getDistance());

        TextView tvStopName = (TextView) convertView.findViewById(R.id.tvStopName);
        TextView tvStopDistance = (TextView) convertView.findViewById(R.id.tvStopDistance);

        tvStopName.setText(stopName);
        tvStopDistance.setText(stopDistance);

        return convertView;

    }

    @Override
    public boolean hasStableIds() {

        return false;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return false;

    }

    private String formatDistance(double distance) {

        String strDistance = (distance >= 1000) ? new DecimalFormat("#.#").format(distance * 0.001) + " Km" : distance + " m";

        return strDistance;

    }

    static class ViewHolder {

        public TextView tvDirection;
        public TextView tvRouteCode;
        public TextView tvRouteName;
        public TextView tvRouteTime;

    }

}
