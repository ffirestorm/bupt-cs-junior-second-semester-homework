package com.animee.forecast.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.animee.forecast.R;
import com.animee.forecast.bean.WeatherBean;
import com.animee.forecast.db.DatabaseBean;
import com.animee.forecast.juhe.JHTempBean;
import com.google.gson.Gson;

import java.util.List;

public class CityManagerAdapter extends BaseAdapter{
    Context context;
    List<DatabaseBean>mDatas;

    public CityManagerAdapter(Context context, List<DatabaseBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city_manager,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        DatabaseBean bean = mDatas.get(position);
        holder.cityTv.setText(bean.getCity());

        JHTempBean jhTempBean = new Gson().fromJson(bean.getContent(), JHTempBean.class);
        JHTempBean.ResultBean jhResult = jhTempBean.getResult();
        JHTempBean.ResultBean.RealtimeBean jhRealtime = jhResult.getRealtime();
        JHTempBean.ResultBean.FutureBean jhTodayBean = jhResult.getFuture().get(0);
//        获取今日天气情况
        holder.conTv.setText("天气:"+jhRealtime.getInfo());

        holder.currentTempTv.setText(jhRealtime.getTemperature()+"℃");
        holder.windTv.setText(jhRealtime.getDirect()+jhRealtime.getPower());
        holder.tempRangeTv.setText(jhTodayBean.getTemperature());
        return convertView;
    }

    class ViewHolder{
        TextView cityTv,conTv,currentTempTv,windTv,tempRangeTv;
        public ViewHolder(View itemView){
            cityTv = itemView.findViewById(R.id.item_city_tv_city);
            conTv = itemView.findViewById(R.id.item_city_tv_condition);
            currentTempTv = itemView.findViewById(R.id.item_city_tv_temp);
            windTv = itemView.findViewById(R.id.item_city_wind);
            tempRangeTv = itemView.findViewById(R.id.item_city_temprange);

        }
    }
}
