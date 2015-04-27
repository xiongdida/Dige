package com.XD.street.adapter;

import java.util.ArrayList;

import com.XD.street.R;
import com.XD.street.data.ThreadData;
import com.XD.street.data.ThreadReply;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThreadDataAdapter extends BaseAdapter{

	public ThreadData threadTopic;
	public ArrayList<ThreadReply> array;
	public boolean topicFirst;
	private LayoutInflater mInflater;
	
	
	final int VIEW_TYPE = 2;  
    final int TOPIC = 0;  
    final int REPLY = 1; 


    @Override 
    public int getItemViewType(int position) {
    	if(topicFirst && position == 0)  
            return TOPIC;  
        else return REPLY;  

    }
    
    @Override  
    public int getViewTypeCount() {  
        return 2;  
    }
    
	public ThreadDataAdapter(Context context) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);//布局填充器 
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(topicFirst)
			return array.size() + 1;
		else return array.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		if(topicFirst)
		{switch(type)  
	        {
				case TOPIC : return threadTopic;
				case REPLY : return array.get(position - 1);
				default : return null;
	        }
		}
		else
		{
			return array.get(position);
		}
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		//无convertView，需要new出各个控件  
        if(convertView == null)  
        {
        	//按当前所需的样式，确定new的布局  
            switch(type)  
            {
                case TOPIC:  
	                convertView = mInflater.inflate(R.layout.content_topic_item, parent, false);   
	                ThreadData data = threadTopic;
	    			TextView title = (TextView)convertView.findViewById(R.id.topic_title);
	    			TextView date = (TextView)convertView.findViewById(R.id.topic_date);
	    			TextView author = (TextView)convertView.findViewById(R.id.topic_author);
	    			TextView replies = (TextView)convertView.findViewById(R.id.reply_num);
	    			TextView lights = (TextView)convertView.findViewById(R.id.light_num);
	    			TextView lz = (TextView)convertView.findViewById(R.id.lz);
	    			TextView content = (TextView)convertView.findViewById(R.id.topic_content);
	    			
	    			title.setText(data.subject);
	    			lights.setText(data.lights + "亮");
	    			replies.setText(data.replies + "回复");
	    			System.out.println(data.content);
	    			System.out.println(Html.fromHtml(data.content));
	    			content.setText(Html.fromHtml(data.content));//html转变为文字	    			
	    			date.setText(data.postdate);
	    			author.setText(data.author);	    			
	    			lz.setText("楼主");
	    			
	    			break;  
                case REPLY: 
                	convertView = mInflater.inflate(R.layout.content_reply_item, parent, false);   
                	ThreadReply reply;
                	if(topicFirst)
                		reply = array.get(position - 1);
                	else reply = array.get(position);
        			TextView reply_author = (TextView)convertView.findViewById(R.id.reply_author);
        			TextView floor = (TextView)convertView.findViewById(R.id.reply_floor);
        			TextView reply_lights = (TextView)convertView.findViewById(R.id.reply_lights);
        			TextView reply_content = (TextView)convertView.findViewById(R.id.reply_content);
        			
        			reply_author.setText(reply.author);
        			reply_lights.setText(String.format("亮了(%s)", reply.lights));
        			floor.setText(String.format("%s楼", reply.floor));
        			reply_content.setText(Html.fromHtml(reply.content));//html转变为文字
        			Log.v("XDreply","nullreply" + reply.floor);
                    break; 
            }  
        }
        else
        {
            switch(type)  
            {
                case TOPIC:
	                ThreadData data = threadTopic;
	    			TextView title = (TextView)convertView.findViewById(R.id.topic_title);
	    			TextView date = (TextView)convertView.findViewById(R.id.topic_date);
	    			TextView author = (TextView)convertView.findViewById(R.id.topic_author);
	    			TextView replies = (TextView)convertView.findViewById(R.id.reply_num);
	    			TextView lights = (TextView)convertView.findViewById(R.id.light_num);
	    			TextView lz = (TextView)convertView.findViewById(R.id.lz);
	    			TextView content = (TextView)convertView.findViewById(R.id.topic_content);
	    			
	    			title.setText(data.subject);
	    			lights.setText(data.lights + "亮");
	    			replies.setText(data.replies + "回复");
	    			content.setText(Html.fromHtml(data.content));    			
	    			date.setText(data.postdate);
	    			author.setText(data.author);	    			
	    			lz.setText("楼主");
	    			break;  
                case REPLY:
                	ThreadReply reply;
                	if(topicFirst)
                		reply = array.get(position - 1);
                	else reply = array.get(position);
        			TextView reply_author = (TextView)convertView.findViewById(R.id.reply_author);
        			TextView floor = (TextView)convertView.findViewById(R.id.reply_floor);
        			TextView reply_lights = (TextView)convertView.findViewById(R.id.reply_lights);
        			TextView reply_content = (TextView)convertView.findViewById(R.id.reply_content);
        			
        			reply_author.setText(reply.author);
        			reply_lights.setText(String.format("亮了(%s)", reply.lights));
        			floor.setText(String.format("%s楼", reply.floor));
        			reply_content.setText(Html.fromHtml(reply.content));
        			Log.v("XDreply","elsereply" + reply.floor);
                    break; 
            }  
        }  
        return convertView;
	}

}
