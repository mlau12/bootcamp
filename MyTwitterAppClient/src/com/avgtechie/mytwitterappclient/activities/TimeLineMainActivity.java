package com.avgtechie.mytwitterappclient.activities;

import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.avgtechie.mytwitterappclient.R;
import com.avgtechie.mytwitterappclient.adapters.TweetsAdapter;
import com.avgtechie.mytwitterappclient.listeners.EndlessScrollListener;
import com.avgtechie.mytwitterappclient.models.Tweet;
import com.avgtechie.mytwitterappclient.restclients.RestClientApp;
import com.loopj.android.http.JsonHttpResponseHandler;

public class TimeLineMainActivity extends Activity {

	protected static final String TAG = "TimeLineActivity";
	List<Tweet> tweets;
	ListView lvTweets;
	TweetsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_line);
		lvTweets = (ListView) findViewById(R.id.lv_tweets);
		loadTweets();
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void loadMore(int page) {
				loadMoreTweets(page);
			}
		});
		//TODO Add username here 
		getActionBar().setTitle("@Username");

	}

	public void loadTweets() {
		RestClientApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonArray) {
				Log.d(TAG, jsonArray.toString());
				tweets = Tweet.fromJson(jsonArray);
				adapter = new TweetsAdapter(getBaseContext(), tweets);
				lvTweets.setAdapter(adapter);
				EndlessScrollListener.page++;
			}
		}, EndlessScrollListener.page);
	}

	public void loadMoreTweets(int page) {
		RestClientApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonArray) {
				List<Tweet> moreTweets = Tweet.fromJson(jsonArray);
				tweets.addAll(moreTweets);
				moreTweets.clear();
				adapter.notifyDataSetChanged();
				EndlessScrollListener.load = true;
				EndlessScrollListener.page++;
			}
		}, page);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case R.id.item_refresh:
			Toast.makeText(this, "Item Refresh", Toast.LENGTH_SHORT).show();
			break;

		case R.id.item_compose:
			// Toast.makeText(this, "Item Compose", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, TweetComposeActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.time_line, menu);
		return true;
	}

}