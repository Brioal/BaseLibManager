
##To get a Git project into your build:

#Step 1. Add the JitPack repository to your build file
##Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
	
```

##Step 2. Add the dependency[![](https://jitpack.io/v/Brioal/BaseLibManager.svg)](https://jitpack.io/#Brioal/BaseLibManager)
```
	dependencies {
	        compile 'com.github.Brioal:BaseLibManager:xxx'
	}
	
	```
##BaseDialogFragmnent的使用实例
```
public class AddCollectFragment extends BaseDialogFragment {


    @Override
    public void initVar() {
       //初始化数据
    }

    @Override
    public void initView() {
        mRootView = LayoutInflater.from(getActivity()).inflate(R.layout.fra_add_collect, null, false);
       
    }

    @Override
    public void initWindow(WindowManager.LayoutParams params) {
        params.gravity = Gravity.BOTTOM;//设置方向
        params.width = getActivity().getWindowManager().getDefaultDisplay().getWidth();//设置 宽度
        params.height = getActivity().getWindowManager().getDefaultDisplay().getHeight() * 2 / 3;//设置高度
    }

    @Override
    public void initAnimation(Dialog dialog) {
        dialog.getWindow().setWindowAnimations(R.style.AnimationSlideBottom);//设置启动动画
    }

    @Override
    public void changeTheme() {
    	//默认为透明圆角背景
        mTheme = R.style.WhiteBackDialog;//纯白背景
    }


```
