package kr.ac.kaist.cs492.dontinsult;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    /**
     *
     */
    private SoundManager mSoundManager = null;
    private LvAdapter mLvAdapter;

    /**
     *
     */
    private ListView mLv;
    private Button mRecordBtn = null;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        /**
         *
         */
        mRecordBtn = (Button) findViewById(R.id.record_btn);
        mLv = (ListView) findViewById(R.id.lv);

        /*
         * ListView Setting
		 */
        mLv.addFooterView(getLayoutInflater().inflate(R.layout.main_activity_lv_footer, null));
        ArrayList<Insult> stores = new ArrayList<Insult>();
        mLvAdapter = new LvAdapter(this, R.layout.main_activity_lv, stores);
        mLv.setAdapter(mLvAdapter);

        /**
         *
         */
        mSoundManager = new SoundManager(mRecordBtn, mLvAdapter);

    }

    /**
     * ListView Apdater Setting
     */
    public class LvAdapter extends ArrayAdapter<Insult> {
        private static final String TAG = "MainActivity LvAdapter";
        public ArrayList<Insult> insults;
        private ViewHolder viewHolder = null;
        private int textViewResourceId;


        public LvAdapter(Activity context, int textViewResourceId,
                         ArrayList<Insult> stores) {
            super(context, textViewResourceId, stores);

            this.textViewResourceId = textViewResourceId;
            this.insults = stores;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return insults.size();
        }

        @Override
        public Insult getItem(int position) {
            return insults.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

			/*
             * UI Initiailizing : View Holder
			 */

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(textViewResourceId, null);

                viewHolder = new ViewHolder();

                viewHolder.mHeaderView = convertView.findViewById(R.id.header_view);
                viewHolder.mHeaderTv = (TextView) convertView.findViewById(R.id.header_tv);

                viewHolder.mContentView = convertView.findViewById(R.id.content_view);
                viewHolder.mPlayBtn = (Button) convertView.findViewById(R.id.play_btn);
                viewHolder.mDescriptionTv = (TextView) convertView.findViewById(R.id.description_tv);


                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Insult insult = this.getItem(position);

			/*
             * Data Import and export
			 */
            if (insult.is_header()) {
                viewHolder.mHeaderView.setVisibility(View.VISIBLE);
                viewHolder.mContentView.setVisibility(View.GONE);
                viewHolder.mHeaderTv.setText(insult.getName());
            } else {
                viewHolder.mHeaderView.setVisibility(View.GONE);
                viewHolder.mContentView.setVisibility(View.VISIBLE);
                viewHolder.mDescriptionTv.setText(insult.getName());
                viewHolder.mPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSoundManager.startPlaying(insult.getFile_name());
                    }
                });
            }

            return convertView;
        }

        private class ViewHolder {

            View mHeaderView;
            TextView mHeaderTv;

            View mContentView;
            TextView mDescriptionTv;
            Button mPlayBtn;
        }

    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();
    }
}
