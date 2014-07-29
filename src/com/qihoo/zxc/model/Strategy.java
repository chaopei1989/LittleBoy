package com.qihoo.zxc.model;

public class Strategy {
	public String id;
	public String title;
	public String url;
	public String s_title;
	public String thumbnail;
	public String channel;
	public String channel_url;
	public long re_time;
	public String more;
	public Strategy(String id, String title, String url, String s_title,
			String thumbnail, String channel, String channel_url, long re_time,
			String more) {
		super();
		this.id = id;
		this.title = title;
		this.url = url;
		this.s_title = s_title;
		this.thumbnail = thumbnail;
		this.channel = channel;
		this.channel_url = channel_url;
		this.re_time = re_time;
		this.more = more;
	}
}
