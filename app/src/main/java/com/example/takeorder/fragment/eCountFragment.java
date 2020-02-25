package com.example.takeorder.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.takeorder.R;
import com.example.takeorder.item.ListViewItem;

import java.util.ArrayList;

public class eCountFragment extends Fragment {

    private static final int ITEM_VIEW_MENU = 0;
    private static final int ITEM_VIEW_COUNT = 1;

    private ListView listView;

    int hazelnutAmericanoData ;

    private final int MSG_MENU = 0;

    String menu;

    int hazelnutAmericanoDefault;

    public static Handler handler;

    SharedPreferences sharedPreferences;
    ListViewAdapter adapter;
    ArrayList<ListViewItem> itemList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemList = new ArrayList<>();

        sharedPreferences = getContext().getSharedPreferences("take order", Context.MODE_PRIVATE);
        hazelnutAmericanoData = sharedPreferences.getInt("헤이즐넛 아메리카노", hazelnutAmericanoDefault);

        addItemAll();

        adapter = new ListViewAdapter(getContext(), itemList);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_e_count, container, false);

        listView = root.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_MENU:
                        menu = msg.getData().getString("count");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        int menuCount = sharedPreferences.getInt(menu, 0);
                        menuCount++;
                        editor.putInt(menu, menuCount);
                        editor.commit();

                        //ui 처리
                        itemList.clear();
                        addItemAll();
                        adapter.notifyDataSetChanged();

                        break;
                }//메세지를 받으면 처리해야 함.
            }
        };
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) { super.onViewCreated(view, savedInstanceState);}

    @Override
    public void onResume() { super.onResume(); }



    public void addItem(String drinkKor, String drinkEng) {
        ListViewItem item = new ListViewItem();

        item.setDrinkKorea(drinkKor);
        item.setDrinkEnglish(drinkEng);
        item.setType(ITEM_VIEW_MENU);

        itemList.add(item);
    }

    public void addItem(String drink, int count) {
        ListViewItem item = new ListViewItem();
        int result = sharedPreferences.getInt(drink, count);

        item.setDrink(drink);
        item.setCount(result);
        item.setType(ITEM_VIEW_COUNT);

        itemList.add(item);
//        item.setType(ITEM_VIEW_COUNT);
    }

    private void addItemAll() {
        addItem(getString(R.string.coffee_korea), getString(R.string.coffee_english));
        addItem(getString(R.string.americano), 0);
        addItem(getString(R.string.hazelnut_americano), hazelnutAmericanoData);
        addItem(getString(R.string.cafe_latte), 0);
        addItem(getString(R.string.cappuccino), 0);
        addItem(getString(R.string.caramel_macciatto), 0);
        addItem(getString(R.string.cafe_mocha), 0);
        addItem(getString(R.string.hazelnut_latte), 0);
        addItem(getString(R.string.vanilla_latte), 0);
        addItem(getString(R.string.chocolate_latte), 0);

        addItem(getString(R.string.blended_korea), getString(R.string.blended_english));
        addItem(getString(R.string.caramel_pointccino), 0);
        addItem(getString(R.string.mocha_pointccino), 0);
        addItem(getString(R.string.yogurt_pointccino), 0);
        addItem(getString(R.string.chocolate_pointccino), 0);

        addItem(getString(R.string.ade_korea), getString(R.string.ade_english));
        addItem(getString(R.string.lemon_ade), 0);
        addItem(getString(R.string.citron_ade), 0);
        addItem(getString(R.string.grapefruit_ade), 0);

        addItem(getString(R.string.tea_korea), getString(R.string.tea_english));
        addItem(getString(R.string.earl_grey), 0);
        addItem(getString(R.string.green_tea), 0);
        addItem(getString(R.string.chamomile), 0);
        addItem(getString(R.string.peppermint), 0);
        addItem(getString(R.string.citrus_tea), 0);
        addItem(getString(R.string.lemon_tea), 0);
        addItem(getString(R.string.grapefruit_tea), 0);
        addItem(getString(R.string.ginger_with_jujube), 0);
        addItem(getString(R.string.flower_tea), 0);
        addItem(getString(R.string.milk_tea), 0);

    }

    public class ListViewAdapter extends BaseAdapter {

        private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();
        private static final int ITEM_VIEW_MENU = 0;
        private static final int ITEM_VIEW_COUNT = 1;
        private static final int ITEM_VIEW_TYPE_MAX = 2;
        SharedPreferences sharedPreferences;
        Context mContext;
        ArrayList<ListViewItem> mList;

        public ListViewAdapter(Context context, ArrayList<ListViewItem> list) {
            super();
            this.mContext = context;
            this.mList = list;
        }


        @Override
        public int getItemViewType(int position) {
            return mList.get(position).getType();
        }

        @Override
        public int getViewTypeCount() {
            return ITEM_VIEW_TYPE_MAX;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Context context = parent.getContext();
            int viewType = getItemViewType(position);

            sharedPreferences = context.getSharedPreferences("take order", Context.MODE_PRIVATE);

            ListViewItem listViewItem = mList.get(position);

            switch (viewType) {
                case ITEM_VIEW_MENU:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.listview_menu_item, parent, false);
                    }

                    TextView drinkKoreaView = convertView.findViewById(R.id.drink_korea);
                    drinkKoreaView.setText(listViewItem.getDrinkKorea());

                    TextView drinkEnglishView = convertView.findViewById(R.id.drink_english);
                    drinkEnglishView.setText(listViewItem.getDrinkEnglish());

                    break;

                case ITEM_VIEW_COUNT:
                    if (convertView == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.listview_count_item, parent, false);
                    }
                    TextView drinkView = convertView.findViewById(R.id.drink);
                    drinkView.setText(listViewItem.getDrink());
                    String text = drinkView.getText().toString();

                    TextView countView = convertView.findViewById(R.id.count);
                    countView.setText("" + listViewItem.getCount());
                    break;
            }
            return convertView;

        }
    }
}
