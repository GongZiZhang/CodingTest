# CodingTest
#项目技术选型与架构:
1. Kotlin+MVVM+Jetpack(LiveData、 Lifecycle、ViewBinding)
2. 网络库选择：Okhttp、Retrofit2
3. 图片库：Coil
6. 其他第三方辅助库：Gson，BaseRecyclerViewAdapterHelper，viewbinding-base-ktx

# 项目UML图    

![image](https://user-images.githubusercontent.com/17826038/116848991-6518ff00-ac20-11eb-8583-30ab31126a84.png)

# 设计考虑

MVVM模式为google官方推荐的android设计模式，优势是能将界面跟数据操作处理独立开，解耦了UI层跟数据层的依赖。而kotlin推出的Jetpack库中的livedata和lifecycle在mvvm模式中有着天然的优势，让我们在UI及数据处理时，不用过多关心UI的生命周期。

# 其他描述

1、web页面的加载使用了自带的webview控件，进行了js操作，图片大小设置缓存及视频播放及全屏的处理；
2、基础activity类中使用第三方的viewbinding-base-ktx库对ViewBinding进行了封装，省去了子类的bind步骤；
3、网络层使用retrofit2框架，利用kotlin的协程及高阶函数、livedata进行了封装，方便了使用。
