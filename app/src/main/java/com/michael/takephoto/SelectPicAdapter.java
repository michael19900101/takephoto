package com.michael.takephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.michael.takephoto.util.SelectPath;

import java.io.IOException;
import java.util.List;

public class SelectPicAdapter extends BaseAdapter {
	private List<String> list;
	protected LayoutInflater mInflater;
	
	@Override
	public int getCount() {
		return list.size()+1;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public SelectPicAdapter(Context context, List<String> list, GridView mGridView){
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_published_grida, parent, false);
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.item_grida_image);

			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}		
		
		if (list.size() == position) {
		    viewHolder.mImageView.setImageResource(R.drawable.icon_addpic_unfocused);
		    if (position == 9) {
		        viewHolder.mImageView.setVisibility(View.GONE);
		    }
		} else {
		      String path = list.get(position);
		      viewHolder.mImageView.setTag(path); 
		      Bitmap bitmap = null;
              try {
                  //获取缩略图
                  bitmap = SelectPath.revitionImageSize(path);
              } catch (IOException e) {
                  e.printStackTrace();
              }
		        if(bitmap != null){
		            viewHolder.mImageView.setImageBitmap(bitmap);
		        }else{
		            viewHolder.mImageView.setImageResource(R.drawable.icon_addpic_unfocused);
		        }
		}	
		return convertView;
	}

	public static class ViewHolder{
		public ImageView mImageView;
	}	
}
