package ysg.gdcp.cn.swipelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ysg.gdcp.cn.swipelayout.manager.SwipeLayoutManager;

public class MainActivity extends AppCompatActivity {

    private ListView mLv;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
    }

    private void initData() {
        for (int i = 0; i < 30; i++) {
            list.add("郭思思" + i);
        }
        mLv.setAdapter(new MyAdapter());
        mLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    SwipeLayoutManager.getSwipeLayoutManager().closeCurrent();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initViews() {
        mLv = (ListView) findViewById(R.id.main_lv);

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.main_item, null);
            }
            ViewHolder holder = ViewHolder.getViewHolder(convertView);
            holder.tvName.setText(list.get(position));
            holder.swipeLayout.setTag(position);
            holder.swipeLayout.setOnSwipeStateChangeListener(new SwipeLayout.OnSwipeStateChangeListener() {
                @Override
                public void onOpen(Object tag) {
                    Toast.makeText(MainActivity.this, "第"+(Integer)tag+"打开", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onClose(Object tag) {
                    Toast.makeText(MainActivity.this, "第"+(Integer)tag+"关闭", Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvDeletel;
        SwipeLayout swipeLayout;

        public ViewHolder(View convertView) {
            tvName = (TextView) convertView.findViewById(R.id.tv_name);
            tvDeletel = (TextView) convertView.findViewById(R.id.tv_delete);
            swipeLayout =(SwipeLayout)convertView.findViewById(R.id.swiplayout);
        }

        public static ViewHolder getViewHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }
}
