package com.grandblanchs.gbhs;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.TreeSet;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;



public class TwitterFeed extends Fragment {



    private OnFragmentInteractionListener mListener;

    TwitterAdapter adapter;

    //Declaration of ListView lst_feed, so it can be referenced throughout twitter.java
    ListView lst_feed;
    ProgressBar prog;

    public TwitterFeed() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.twitter, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            prog = (ProgressBar) getView().findViewById(R.id.progTwitter);

            //Finds the ListView lst_feed on the GUI form
            lst_feed = (ListView) getView().findViewById(R.id.list);

            //Since internet dependant tasks cannot be performed on the main method, we execute a new one called twitterTimeline
            new TwitterTimeline().execute();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {}


    private class TwitterTimeline extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //Authenticates this app with my specific account codes. This will need to be changed to GBHS twitter account codes.
            twitter4j.Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(getString(R.string.CONSUMER_KEY), getString(R.string.CONSUMER_KEY_SECRET));

            AccessToken oathAccessToken = new AccessToken(getString(R.string.twitterAccessToken), getString(R.string.twitterAccessTokenSecret));
            twitter.setOAuthAccessToken(oathAccessToken);

            //Creates a string array of length 20 for the twenty twitter posts to be stored in.
            //This is needed because it's a lot easier to populate a ListView with a string array
            final String [] testing = new String[20];

            //A try-catch for the twitter4j method, since I can't add 'Throws twitter exception' on this method
            try {
                //Retrieves the last 20 tweets from the user 'GrandBlancPride' and puts them into a responseList
                ResponseList<twitter4j.Status> statuses = twitter.getUserTimeline("GrandBlancPride");
                //This for loop takes the text from every member of this response list and moves it to the string array
                for(int i = 0; i < statuses.size(); i++) {
                    testing[i] = statuses.get(i).getText();
                }

                adapter = new TwitterAdapter();

                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < testing.length; ++i) {
                    list.add(testing[i]);
                    adapter.addItem(testing[i]);
                }

                //Since the UI cannot be changed without this,
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Populates lst_feed with string array testing
                            lst_feed.setAdapter(adapter);
                        }
                    });
                }

            } catch (final TwitterException e) {
                if (getActivity() != null) {
                    final Context context = getActivity().getApplicationContext();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(context, getString(R.string.NoTwitter) + " " + e.getErrorCode() + ")", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (getActivity() != null) {
                super.onPostExecute(aVoid);
                prog.setVisibility(View.GONE);
            }
        }
    }

    //TwitterAdapter class
    private class TwitterAdapter extends BaseAdapter {
        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;

        private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

        public TwitterAdapter() {
            if (getActivity() != null) {
                mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        public void addItem(final String item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item) {
            mData.add(item);
            //Save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Calendar.ViewHolder holder = null;
            int type = getItemViewType(position);
            holder = new Calendar.ViewHolder();
            /*No 'if (convertView == null)' statement to prevent view recycling
            (views must remain fixed)*/
            switch (type) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.itemlist, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    break;
            }
            convertView.setTag(holder);
            holder.textView.setText(mData.get(position));
            return convertView;
        }
    }

}