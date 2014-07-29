package com.qihoo.zxc.view;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.qihoo.zxc.R;
import com.qihoo.zxc.animation.Rotate3dAnimation;
import com.qihoo.zxc.constant.Constant;
import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.constant.Constant.VIEW_POSITION;
import com.qihoo.zxc.constant.Constant.VIEW_TYPE;
import com.qihoo.zxc.model.PackageHidden;
import com.qihoo.zxc.service.FloatWindowService;
import com.qihoo.zxc.service.MyWindowManager;
import com.qihoo.zxc.utils.ProcessUtil;
import com.qihoo.zxc.utils.Toast;
import com.qihoo.zxc.view.StrategyView.STATUS;

import android.R.menu;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class Views {
	final String TAG = "Views";
	Context context;
	boolean isAnimating = false;
	ImageView menuImageView;

	public Views(Context context, ImageView menuImageView) {
		this.context = context;
		this.menuImageView = menuImageView;
	}

	ImageView speedupImageView, hideImageView, strategyImageView,
			giftImageView;

	int getVisibility() {
		if (View.GONE == speedupImageView.getVisibility()) {
			return View.GONE;
		}
		if (View.GONE == hideImageView.getVisibility()) {
			return View.GONE;
		}
		if (View.GONE == strategyImageView.getVisibility()) {
			return View.GONE;
		}
		if (View.INVISIBLE == speedupImageView.getVisibility()) {
			return View.INVISIBLE;
		}
		if (View.INVISIBLE == hideImageView.getVisibility()) {
			return View.INVISIBLE;
		}
		if (View.INVISIBLE == strategyImageView.getVisibility()) {
			return View.INVISIBLE;
		}
		return View.VISIBLE;
	}

	void setVisibility(final int visibility) {
		if (visibility == View.GONE) {
			if (getVisibility() == View.GONE) {
				return;
			}
		}
		Log.e(TAG, "setVisibility " + visibility);

		final ValueAnimator valueAnimator = ValueAnimator.ofInt(100, 0);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float v = (int) animation.getAnimatedValue() / 100.0f;
				strategyImageView.setAlpha(v);
				speedupImageView.setAlpha(v);
				hideImageView.setAlpha(v);
				giftImageView.setAlpha(v);
			}
		});
		valueAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				isAnimating = true;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				strategyImageView.setVisibility(visibility);
				hideImageView.setVisibility(visibility);
				speedupImageView.setVisibility(visibility);
				giftImageView.setVisibility(visibility);
				isAnimating = false;
				menuImageView
						.setImageResource(MyWindowManager.floatView.now[0]);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		if (visibility == View.GONE) {
			if (getVisibility() == View.GONE) {
				return;
			}
			Log.e(TAG, "getVisibility()!=View.GONE");
			if (!isAnimating) {
				valueAnimator.setDuration(200);
				valueAnimator.start();
			}
		} else {
			menuImageView.clearAnimation();
			menuImageView.setImageResource(R.drawable.cancl);
			speedupImageView.setVisibility(visibility);
			hideImageView.setVisibility(visibility);
			strategyImageView.setVisibility(visibility);
			MyWindowManager.floatView.hideGift();

			speedupImageView.setAlpha(Constant.FULL_ALPHA);
			hideImageView.setAlpha(Constant.FULL_ALPHA);
			strategyImageView.setAlpha(Constant.FULL_ALPHA);
			giftImageView.setAlpha(Constant.FULL_ALPHA);
		}
	}
}

public class FloatView extends FrameLayout {

	final String TAG = "FloatView";

	Context context;

	ViewGroup menuLayout;

	ImageView menuImageView;

	Views menuExpandLayout;

	public Constant.VIEW_TYPE type = VIEW_TYPE.GAME;

	Constant.VIEW_POSITION position = VIEW_POSITION.LEFT;

	int windowWidth, windowHeight, statusHeight;

	public int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	public void hideGift() {
		if (0 < LittleBoyApplication.getInstance().gamesHiddenMap
				.get(FloatWindowService.lastGame).hasGift) {
			if (menuExpandLayout.getVisibility() == View.VISIBLE)
				menuExpandLayout.giftImageView.setVisibility(View.VISIBLE);
			else
				menuExpandLayout.giftImageView.setVisibility(View.GONE);
		} else {
			menuExpandLayout.giftImageView.setVisibility(View.GONE);
			now[0] = R.drawable.float_view;
			menuImageView.setImageResource(now[0]);
		}
	}

	public void animateGift(boolean r) {
		menuImageView.invalidate();
		if (0 >= LittleBoyApplication.getInstance().gamesHiddenMap
				.get(FloatWindowService.lastGame).hasGift)
			return;
		if (menuExpandLayout.getVisibility() == View.VISIBLE) {
			return;
		}
		menuImageView.clearAnimation();
		Rotate3dAnimation animation;
		if (r) {
			animation = new Rotate3dAnimation(0, 180,
					menuImageView.getWidth() / 2,
					menuImageView.getHeight() / 2, 0, false,
					R.drawable.gift_view, menuImageView, now);
		} else {
			animation = new Rotate3dAnimation(180, 360,
					menuImageView.getWidth() / 2,
					menuImageView.getHeight() / 2, 0, false,
					R.drawable.float_view, menuImageView, now);
		}
		animation.setDuration(1000);
		menuImageView.setAnimation(animation);
		animation.start();
	}

	int[] now = new int[] { R.drawable.float_view };

	public void inGame() {
		type = VIEW_TYPE.GAME;
		if (menuExpandLayout.getVisibility() == GONE) {
			menuImageView.setImageResource(now[0]);
		} else {
			menuImageView.setImageResource(R.drawable.cancl);
		}
	}

	public void pause() {
		type = VIEW_TYPE.PAUSE;
		menuImageView.setImageResource(R.drawable.back_view);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public FloatView(Context context) {
		super(context);
		this.context = context;
		findAllViews();
		setAllListener();
	}

	private void setAllListener() {
		menuImageView.setOnTouchListener(new OnTouchListener() {
			int tempX, tempY;
			int vx;
			int vy;
			boolean closed = true;
			boolean move = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				int rawX = (int) event.getRawX();
				int rawY = (int) event.getRawY();
				FloatWindowService.resetAlphaTime();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					move = false;
					tempX = rawX;
					tempY = rawY;
					vx = MyWindowManager.floatViewParams.x;
					vy = MyWindowManager.floatViewParams.y;
					if (menuExpandLayout.getVisibility() == View.VISIBLE) {
						closed = false;
						menuExpandLayout.setVisibility(View.GONE);
					} else {
						closed = true;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (!move && x > 0 && x < menuImageView.getWidth() && y > 0
							&& y < menuImageView.getHeight()) {
						break;
					}
					move = true;
					int deltaX = rawX - tempX;
					int deltaY = rawY - tempY;
					MyWindowManager.floatViewParams.x = vx + deltaX;
					MyWindowManager.floatViewParams.y = vy + deltaY;
					MyWindowManager.getWindowManager(context).updateViewLayout(
							FloatView.this, MyWindowManager.floatViewParams);
					// menuLayout.layout(l + deltaX, t + deltaY, l+ deltaX +
					// menuLayout.getWidth(),t+ deltaY + menuLayout.getHeight()
					// );
					break;
				case MotionEvent.ACTION_UP:
					if (!move) {
						Log.d(TAG, "position:" + position);
						switch (type) {
						case GAME:
							if (menuExpandLayout.getVisibility() == View.GONE
									&& closed) {
								menuExpandLayout.setVisibility(View.VISIBLE);
							} else {
								menuExpandLayout.setVisibility(View.GONE);
							}

							break;
						case PAUSE:
							// if(null == FloatWindowService.lastGame) break;
							// Intent intent = context.getPackageManager()
							// .getLaunchIntentForPackage(FloatWindowService.lastGame);
							// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// context.startActivity(intent);
							ActivityManager manager = (ActivityManager) context
									.getSystemService(Context.ACTIVITY_SERVICE);
							List<RunningTaskInfo> runningTasks = manager
									.getRunningTasks(10);// Return a list of
															// the
															// tasks
							Log.e(TAG, "pause back");
							for (RunningTaskInfo runningTaskInfo : runningTasks) {
								if (runningTaskInfo.topActivity
										.getPackageName().equals(
												FloatWindowService.lastGame)) {
									manager.moveTaskToFront(
											runningTaskInfo.id,
											ActivityManager.MOVE_TASK_NO_USER_ACTION);
									break;
								}
							}
							break;
						default:
							break;
						}
					} else {
						windowWidth = MyWindowManager.getWindowManager(context)
								.getDefaultDisplay().getWidth();
						windowHeight = MyWindowManager
								.getWindowManager(context).getDefaultDisplay()
								.getHeight();
						statusHeight = getStatusBarHeight(context);
						ValueAnimator resetTran;
						boolean bx = true;
						if (MyWindowManager.floatViewParams.x <= windowWidth / 2) {

							if (MyWindowManager.floatViewParams.y <= windowHeight / 2) {
								if (MyWindowManager.floatViewParams.x <= MyWindowManager.floatViewParams.y) {
									position = VIEW_POSITION.LEFT;
									resetTran = ValueAnimator.ofInt(
											MyWindowManager.floatViewParams.x,
											0);
								} else {
									position = VIEW_POSITION.TOP;
									bx = false;
									resetTran = ValueAnimator.ofInt(
											MyWindowManager.floatViewParams.y,
											0);
								}
							} else {
								if (MyWindowManager.floatViewParams.x <= windowHeight
										- MyWindowManager.floatViewParams.y) {
									position = VIEW_POSITION.LEFT;
									resetTran = ValueAnimator.ofInt(
											MyWindowManager.floatViewParams.x,
											0);
								} else {
									position = VIEW_POSITION.BOTTOM;
									bx = false;
									resetTran = ValueAnimator.ofInt(
											MyWindowManager.floatViewParams.y,
											windowHeight
													- menuLayout.getHeight());
								}
							}

						} else {

							if (MyWindowManager.floatViewParams.y <= windowHeight / 2) {
								if (windowWidth
										- MyWindowManager.floatViewParams.x <= MyWindowManager.floatViewParams.y) {
									position = VIEW_POSITION.RIGHT;
									resetTran = ValueAnimator
											.ofInt(MyWindowManager.floatViewParams.x,
													windowWidth
															- menuLayout
																	.getWidth());
								} else {
									position = VIEW_POSITION.TOP;
									bx = false;
									resetTran = ValueAnimator.ofInt(
											MyWindowManager.floatViewParams.y,
											0);
								}
							} else {
								if (windowWidth
										- MyWindowManager.floatViewParams.x <= windowHeight
										- MyWindowManager.floatViewParams.y) {
									position = VIEW_POSITION.RIGHT;
									resetTran = ValueAnimator
											.ofInt(MyWindowManager.floatViewParams.x,
													windowWidth
															- menuLayout
																	.getWidth());
								} else {
									position = VIEW_POSITION.BOTTOM;
									bx = false;
									resetTran = ValueAnimator.ofInt(
											MyWindowManager.floatViewParams.y,
											windowHeight
													- menuLayout.getHeight());
								}
							}
						}

						final boolean fbx = bx;
						resetTran
								.addUpdateListener(new AnimatorUpdateListener() {
									@Override
									public void onAnimationUpdate(
											ValueAnimator animation) {

										if (fbx) {
											MyWindowManager.floatViewParams.x = (Integer) animation
													.getAnimatedValue();
										} else {
											MyWindowManager.floatViewParams.y = (Integer) animation
													.getAnimatedValue();
										}

										MyWindowManager
												.getWindowManager(context)
												.updateViewLayout(
														FloatView.this,
														MyWindowManager.floatViewParams);
									}
								});
						resetTran.addListener(new AnimatorListener() {
							@Override
							public void onAnimationStart(Animator animation) {
								menuImageView.setEnabled(false);
							}

							@Override
							public void onAnimationRepeat(Animator animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animator animation) {
								menuImageView.setEnabled(true);
							}

							@Override
							public void onAnimationCancel(Animator animation) {
								// TODO Auto-generated method stub

							}
						});
						resetTran.setInterpolator(AnimationUtils
								.loadInterpolator(context,
										android.R.anim.accelerate_interpolator));
						resetTran.setDuration(100);
						resetTran.start();
					}
				default:
					break;
				}
				return true;
			}
		});
		menuExpandLayout.giftImageView
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuExpandLayout.setVisibility(View.GONE);
						Intent intent = context.getPackageManager()
								.getLaunchIntentForPackage("com.qihoo.appstore");
						intent.putExtra("from_out_side","feiyang");
						intent.putExtra("start_activity_index",30);
						intent.putExtra("pkg_name",FloatWindowService.lastGame);
						context.startActivity(intent);
					}
				});
		menuExpandLayout.hideImageView
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuExpandLayout.setVisibility(View.GONE);
						new Thread(new Runnable() {

							@Override
							public void run() {
								if (null == FloatWindowService.lastGame) {
									return;
								}
								LittleBoyApplication.getInstance().myHelper
										.saveAppHidden(
												FloatWindowService.lastGame, 1);
								LittleBoyApplication.getInstance()
										.notifyDataSetChanged();
							}
						}).start();

					}
				});
		menuExpandLayout.strategyImageView
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuExpandLayout.setVisibility(View.GONE);
						PackageHidden packageHidden = LittleBoyApplication
								.getInstance().gamesHiddenMap
								.get(FloatWindowService.lastGame);
						if (null != packageHidden) {
							if (packageHidden.strategy.equals("")) {
								Toast.showShort(context, "本游戏无攻略");
							} else {
								MyWindowManager.createStrategyView(context,
										packageHidden.strategy);
								((FloatWindowService) context).handler
										.postDelayed(new Runnable() {

											@Override
											public void run() {
												MyWindowManager.strategyView.status = STATUS.OPEN;
												MyWindowManager.strategyView.titleLayout
														.setVisibility(View.VISIBLE);
												MyWindowManager.strategyView.strategyInnerLayout
														.setVisibility(View.VISIBLE);
											}
										}, 500);

							}
						}
						// Intent intent = new Intent();
						// intent.setPackage("com.qihoo.store");
						// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.putExtra("from_out_side","feiyang");
						// intent.putExtra("start_activity_index",30);
						// intent.putExtra("pkg_name","com.SmartSpace.TheSoulOfSwordFury.Android.qh360");
						// context.startActivity(intent);
						// try {
						// MyWindowManager.screenshot();
						// } catch (NoSuchMethodException |
						// ClassNotFoundException
						// | IllegalAccessException | IllegalArgumentException
						// | InvocationTargetException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
					}
				});
		menuExpandLayout.speedupImageView
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						menuExpandLayout.setVisibility(View.GONE);
						new Thread(new Runnable() {

							@Override
							public void run() {
								ActivityManager activityManager = (ActivityManager) context
										.getSystemService(Context.ACTIVITY_SERVICE);
								List<RunningAppProcessInfo> runningApps = activityManager
										.getRunningAppProcesses();
								final ActivityManager.MemoryInfo memoryBefore = new ActivityManager.MemoryInfo();
								activityManager.getMemoryInfo(memoryBefore);
								for (RunningAppProcessInfo info : runningApps) {
									try {
										if (!ProcessUtil.getHomes(context)
												.contains(info.processName)) {
											ProcessUtil.clearBackgroundProcess(
													info.processName, context);
										}
									} catch (NameNotFoundException e) {
									}
								}
								final ActivityManager.MemoryInfo memoryAfter = new ActivityManager.MemoryInfo();
								activityManager.getMemoryInfo(memoryAfter);

								((FloatWindowService) context).handler
										.post(new Runnable() {
											@Override
											public void run() {
												long av = (memoryAfter.availMem - memoryBefore.availMem)
														* 100
														/ memoryBefore.availMem;
												if (av > 1) {
													Toast.showShort(context,
															"360小玩伴为您加速 " + av
																	+ "%");
												} else {
													Toast.showShort(context,
															"手机状态很好哦");
												}
											}
										});
							}
						}).start();
					}
				});
	}

	public void hideExpandViews() {
		menuExpandLayout.setVisibility(GONE);
	}

	public void hideMenu() {
		menuLayout.setVisibility(INVISIBLE);
	}

	public void apparentMenu() {
		menuLayout.setVisibility(VISIBLE);
	}

	public void fullAlphaMenu() {
		menuLayout.setAlpha(Constant.FULL_ALPHA);
	}

	public void halfAlphaMenu() {
		menuLayout.setAlpha(Constant.HALF_ALPHA);
		menuExpandLayout.setVisibility(GONE);
	}

	private void findAllViews() {
		LayoutInflater.from(context).inflate(R.layout.float_view, this);
		menuLayout = (ViewGroup) findViewById(R.id.menu_layout);
		menuImageView = (ImageView) findViewById(R.id.menu_imageview);
		menuExpandLayout = new Views(context, menuImageView);
		menuExpandLayout.giftImageView = (ImageView) findViewById(R.id.gift_imageview);
		menuExpandLayout.speedupImageView = (ImageView) findViewById(R.id.speedup_imageview);
		menuExpandLayout.strategyImageView = (ImageView) findViewById(R.id.strategy_imageview);
		menuExpandLayout.hideImageView = (ImageView) findViewById(R.id.hide_imageview);
	}

}
