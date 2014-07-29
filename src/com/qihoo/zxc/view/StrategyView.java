package com.qihoo.zxc.view;

import org.json.JSONException;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.qihoo.zxc.R;
import com.qihoo.zxc.adapter.StrategyListAdapter;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.service.FloatWindowService;
import com.qihoo.zxc.service.MyWindowManager;
import com.qihoo.zxc.utils.Toast;

public class StrategyView extends FrameLayout {

	final String TAG = "StrategyView";

	Context context;

	int windowWidth, windowHeight;

	WebView webView;

	ListView strategyListView;

	public ViewGroup strategyLayout;
	public ViewGroup titleLayout;
	public ViewGroup titleCloseLayout;
	public ViewGroup titleBackLayout;
	public ViewGroup strategyInnerLayout;
	ImageView backImageView;
	enum STATUS {
		CLOSE, OPEN
	}
	public STATUS status = STATUS.OPEN;

	ProgressBar progressBar;

	StrategyListAdapter adapter;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public StrategyView(Context context) {
		super(context);
		this.context = context;
		findAllViews();
		setAllListener();
	}

	private void setAllListener() {
		webView.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
		// // webView.getSettings().setDefaultFontSize(5);
		// webView.loadUrl("http://g.mgamer.cn/bydr3/gl/?from=zhushou&gppg=org.cocos2d.fishingjoy3.qihu&dbnm=0");
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);// 使用当前WebView处理跳转
				return true;// true表示此事件在此处被处理，不需要再广播
			}

			@Override
			// 转向错误时的处理
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public void onProgressChanged(WebView view, final int newProgress) {
				progressBar.clearAnimation();
				progressBar.setVisibility(View.VISIBLE);
				int last = progressBar.getProgress();

				ValueAnimator animator = ValueAnimator.ofInt(last, newProgress);
				animator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int v = (int) animation.getAnimatedValue();
						progressBar.setProgress(v);
					}
				});
				animator.addListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						if (newProgress == 100) {
							((FloatWindowService)context).handler.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									progressBar.setVisibility(View.INVISIBLE);
									progressBar.setProgress(0);
								}
							}, 200);
						}
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
				animator.setDuration(100);
				animator.start();
				super.onProgressChanged(view, newProgress);
			}

			// @Override
			// public boolean shouldOverrideUrlLoading(WebView view, String url)
			// {
			// // TODO Auto-generated method stub
			// view.loadUrl(url);// 使用当前WebView处理跳转
			// return true;// true表示此事件在此处被处理，不需要再广播
			// }
			//
			// @Override
			// // 转向错误时的处理
			// public void onReceivedError(WebView view, int errorCode,
			// String description, String failingUrl) {
			// // TODO Auto-generated method stub
			// }
		});
		// strategyLayout.setVisibility(View.GONE);
		strategyListView.setAdapter(adapter);
		titleCloseLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final int w = strategyLayout.getHeight();
				ValueAnimator animator = ValueAnimator.ofInt(w, 0);
				final ViewGroup.LayoutParams params = strategyLayout
						.getLayoutParams();
				animator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int v = (int) animation.getAnimatedValue();
						params.height = v;
						strategyLayout.setLayoutParams(params);
					}
				});
				animator.addListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
						titleBackLayout.setEnabled(false);
						strategyLayout.setEnabled(false);
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						params.height = w;
						strategyLayout.setVisibility(View.GONE);
						strategyLayout.setLayoutParams(params);
						titleBackLayout.setEnabled(true);
						strategyLayout.setEnabled(true);
						MyWindowManager.removeStrategyView(context);
					}

					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
				animator.setInterpolator(AnimationUtils.loadInterpolator(
						context, android.R.anim.decelerate_interpolator));
				animator.setDuration(300);
				animator.start();
				status = STATUS.CLOSE;

			}
		});
		titleLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final int w = strategyInnerLayout.getWidth();
				if (status == STATUS.CLOSE) {
					// ValueAnimator animator = ValueAnimator.ofInt(0,w);
					// animator.addUpdateListener(new AnimatorUpdateListener() {
					// @Override
					// public void onAnimationUpdate(ValueAnimator animation) {
					// int v = (int)animation.getAnimatedValue();
					// ViewGroup.LayoutParams params =
					// strategyListView.getLayoutParams();
					// params.height = v;
					// strategyListView.setLayoutParams(params);
					// }
					// });
					// animator.setDuration(300);
					// animator.start();
					status = STATUS.OPEN;
					strategyInnerLayout.setVisibility(View.VISIBLE);
				} else {
					ValueAnimator animator = ValueAnimator.ofInt(w, 0);
					final ViewGroup.LayoutParams params = strategyInnerLayout
							.getLayoutParams();
					animator.addUpdateListener(new AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							int v = (int) animation.getAnimatedValue();
							params.height = v;
							strategyInnerLayout.setLayoutParams(params);
						}
					});
					animator.addListener(new AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							titleLayout.setEnabled(false);
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							params.height = w;
							strategyInnerLayout.setVisibility(View.GONE);
							strategyInnerLayout.setLayoutParams(params);
							titleLayout.setEnabled(true);
						}

						@Override
						public void onAnimationCancel(Animator animation) {
						}
					});
					animator.setInterpolator(AnimationUtils.loadInterpolator(
							context, android.R.anim.decelerate_interpolator));
					animator.setDuration(300);
					animator.start();
					status = STATUS.CLOSE;
				}
			}
		});
		titleBackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) strategyListView
						.getLayoutParams();
				if (params.leftMargin < 0) {
					final int w = strategyListView.getWidth();
					ValueAnimator animator = ValueAnimator.ofInt(-w, 0);
					animator.addUpdateListener(new AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							int v = (int) animation.getAnimatedValue();
							params.setMargins(v, 0, 0, 0);
							strategyListView.setLayoutParams(params);
						}
					});
					animator.addListener(new AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							titleBackLayout.setEnabled(false);
							backImageView.setVisibility(View.GONE);
						}
						@Override
						public void onAnimationRepeat(Animator animation) {
						}

						@Override
						public void onAnimationEnd(Animator animation) {
						}
						@Override
						public void onAnimationCancel(Animator animation) {
						}
					});
					animator.setDuration(200);
					animator.start();
				}else {
					
				}
			}
		});
		strategyListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Toast.showShort(context, adapter.get(position).url);
				final int w = strategyListView.getWidth();
				ValueAnimator animator = ValueAnimator.ofInt(0, -w);
				animator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						int v = (int) animation.getAnimatedValue();
						LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) strategyListView
								.getLayoutParams();
						params.setMargins(v, 0, 0, 0);
						strategyListView.setLayoutParams(params);
					}
				});
				animator.addListener(new AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
					}
					@Override
					public void onAnimationRepeat(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						titleBackLayout.setEnabled(true);
						backImageView.setVisibility(View.VISIBLE);
					}
					@Override
					public void onAnimationCancel(Animator animation) {
					}
				});
				animator.setDuration(200);
				animator.start();
				webView.loadUrl(adapter.get(position).url);
			}
		});
		titleBackLayout.setEnabled(false);
	}

	private void findAllViews() {
		LayoutInflater.from(context).inflate(R.layout.strategy_view, this);
		webView = (WebView) findViewById(R.id.strategy_webview);
		titleLayout = (ViewGroup) findViewById(R.id.title_linearlayout);
		titleBackLayout = (ViewGroup) findViewById(R.id.title_back_layout);
		titleCloseLayout = (ViewGroup) findViewById(R.id.title_close_layout);
		strategyLayout = (ViewGroup) findViewById(R.id.strategy_layout);
		strategyListView = (ListView) findViewById(R.id.strategy_listview);
		strategyInnerLayout = (ViewGroup) findViewById(R.id.strategy_innerlayout);
		progressBar = (ProgressBar) findViewById(R.id.strategy_progressbar);
		backImageView = (ImageView)findViewById(R.id.back_imageview);
		
		try {
			adapter = new StrategyListAdapter(context,
					LittleBoyApplication.getInstance().gamesHiddenMap
							.get(FloatWindowService.lastGame).strategy);
		} catch (JSONException e) {
			Toast.showShort(context, "攻略出错啦");
			e.printStackTrace();
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			// if (event.getAction() != KeyEvent.ACTION_UP) {
			// break;
			// }
			// Log.e(TAG, "KEYCODE_BACK");
			// if (webView.canGoBack()) {
			// webView.goBack();
			// Log.e(TAG, "goback");
			// } else {
			// Log.e(TAG, "removeStrategyView");
			// MyWindowManager.removeStrategyView(context);
			// }
			return false;
		default:
			break;
		}
		return super.dispatchKeyEvent(event);
	}

}
