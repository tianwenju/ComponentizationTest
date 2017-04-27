## 组件化探究
### 为什么使用组件化
>组件化能够让开发者只需专注自己开发的组件，独立运行自己的模块，节省==编译==时间，减少因别人的问题导致工作被打断的可能。


 * [详情请看](http://blog.csdn.net/dfskhgalshgkajghljgh/article/details/52486383)
 * 好处：
    * 加快迭代速度，各个业务模块组件更加独立，不再因为业务耦合情况，在发版时候，由于互相等待而迟迟不能发布版本。

    * 稳定的公共模块采用依赖库方式，提供给各个业务线使用，减少重复开发和维护工作量。

    * 迭代频繁的业务模块采用组件方式，各业务线研发可以互不干扰、提升协作效率，并控制产品质量。

    * 为新业务随时集成提供了基础，所有业务可上可下，灵活多变。

    * 降低团队成员熟悉项目的成本，降低项目的维护难度。

    * 加快编译速度，提高开发效率
    
### 我们的架构图
* 以前的架构
![image](http://img.blog.csdn.net/20170424100001244?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTE1Mjg2ODU2NTc1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
    * CommonLibs是我的通用libs适合所有的app也就是通用模块
    * libary是我们项目所特有的也就是APP所依赖特有的
* 新的架构图
![image](http://img.blog.csdn.net/20170424100205886?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTE1Mjg2ODU2NTc1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
    * AMESLibs是我们App业务组件的统一入口，它包括Commonlibs和他特有的依赖库
    * 业务基础组件是其他业务组件所需要的一些公共模块，列如产线选择，预警通用，这些是业务组件中要用到的
        * 单独拿出来的好处是一处发生改变，其他依赖它的组件也会发生改变
    * Common组件是通用的APP都需要的组件，eg:登录，更新。它依赖的是CommonLibs通用库。
* 层次图

* ![image](http://img.blog.csdn.net/20170425130133686?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTE1Mjg2ODU2NTc1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
        
### 组件化需要解决的问题
1. application和libary的属性切换问题

    * 我们希望我们在开发模式下，能够单独调用自己的模块，编译成独立的apk,而在主程序中成为一个libary.
    * 解决方法：
    
        子模块build.gradle,代表是Application，能编译成独立的apk
        ```
        def isApplication = false
        
          if (isApplication.toBoolean()) {
          apply plugin: 'com.android.application'
        } else {
          apply plugin: 'com.android.library'
        }
        
        
         if (isApplication.toBoolean()) {
            applicationId "com.delta.faultprocessing"
        }
       
        ```
     通过判断选择不同的插件

2. 解决组件AndroidManifest和主工程AndroidManifest合并的问题
    * 每个组件都有不同的成员开发，这时候组件就是一个完整的app,那么这个组件就一个清单文件，而Android程序只有一个清单文件，当组件作为libary合并到主工程的时候就会有冲突，因为他们都有自己实现application类以及一些属性，都有一个主activity。
    * 解决方法：
    维护两张清单文件，通过布尔值，选择不同的清单文件。
        ```
      sourceSets {
        main {
          if (isApplication.toBoolean()) {
            manifest.srcFile 'src/main/debug/AndroidManifest.xml'
          } else {
            manifest.srcFile 'src/main/AndroidManifest.xml'
          }
        }
      }
        ```
3. 解决资源冲突的问题
    * 在组件被整合到主工程的时候肯定会资源冲突，我们的资源也需要模块化，gradle 提供了一个解决方案
    ```
    resourcePrefix "a_"
    ```
4. application初始化的问题
子模块作为application时，有些初始化的工作需要在application的onCreate方法进行，作为libary的时候调用不到这个oncreate（）
![image](http://img.blog.csdn.net/20170424112556600?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTE1Mjg2ODU2NTc1/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

5. 关于数据库
* 数据库在升级的时候有版本的问题，怎么解决版本号的问题？带解决。
6. 关于activity通信跳转的问题
* 有人要问平常方式就能解决Activity的问题，为什么还要提出这个问题？
    * 在android 开发中我们经常使用的是startActivity()，但是这种方法有个问题，前提就是你要打开的Activity的类已经存在，否则无法通过编译，但是在协同开发过程中，这往往是无法得到满足的。那怎么解决这个问题呢？
* 怎么解决？
    *  web在开发过程中使用url来打开Activity,这可以作为我们的思路。本来想介绍ActivityRouter,但当多模块组件化的时候会有问题，因为，它用apt生产的代码是在同一个包名下，也就是说在各个模块中都会生产一个同名文件，所以会冲突。但是它的思想还是值得我们借鉴。
* ActivityRouter
    *  注解
        
        ```
        @Target({ElementType.TYPE, ElementType.METHOD})
        @Retention(RetentionPolicy.CLASS)
        public @interface Router {
        
            String[] value();
        
            String[] stringParams() default "";
        
            String[] intParams() default "";
        
            String[] longParams() default "";
        
            String[] booleanParams() default "";
        
            String[] shortParams() default "";
        
            String[] floatParams() default "";
        
            String[] doubleParams() default "";
        
            String[] byteParams() default "";
        
            String[] charParams() default "";
        
            String[] transfer() default "";
        }
        
        ```
    * 用法：
        1. 在我们需要跳转通的activivty或者方法上加上注解

        ```
        @Router(value = "productLine", stringParams = "o")
        public class ProductLineActivity extends DumpExtrasActivity {
        
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                //setContentView(R.layout.activity_produceline);
            }
        }
        ```
        2. 调用方法
        textview 传入的参数是 router://productLine?id=53789&amp;o=you+are+best
        ```
        Routers.openForResult(MainActivity.this, ((TextView) view).getText().toString(), 100);
        ```
    * 原理分析：
    我们程序的入口是Routers.openForResult方法，所以这也是我们的突破点。
     我们点击去定位到
        ```
         private static boolean doOpen(Context context, Uri uri, int requestCode) {
        initIfNeed();
        Path path = Path.create(uri);
        for (Mapping mapping : mappings) {
            if (mapping.match(path)) {
                if (mapping.getActivity() == null) {
                    mapping.getMethod().invoke(context, mapping.parseExtras(uri));
                    return true;
                }
                Intent intent = new Intent(context, mapping.getActivity());
                intent.putExtras(mapping.parseExtras(uri));
                intent.putExtra(KEY_RAW_URL, uri.toString());
                if (!(context instanceof Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (requestCode >= 0) {
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    } else {
                        throw new RuntimeException("can not startActivityForResult context " + context);
                    }
                } else {
                    context.startActivity(intent);
                }
                return true;
            }
        }
        return false;}
    
        ```
    1. initIfNeed();初始化方法
        ```
        private static void initIfNeed() {
        if (!mappings.isEmpty()) {
            return;
        }
        RouterInit.init();
        sort(); 
            
        }
        ```
        * private static List<Mapping> mappings = new ArrayList<>();
       走到这里你要问 Maping是啥？
       Mapping是一个映射类里面保存了我们的uri对应的activity 或者是我们调用的方法，或者是参数类型
       *  RouterInit.init();有人要问这个RouterInit在哪？如果我们没有buid这个类是不会生产的，它是由我们的apt来生成的 
       ```
       public final class RouterInit {
       public static final void init() {
       RouterMapping.map();
          }
        }
       ```
       RouterMapping这个类也是我们的apt帮我们生成的     
       ```
       public final class RouterMapping {
         public static final void map() {
        java.util.Map<String,String> transfer = null;
        com.github.mzule.activityrouter.router.ExtraTypes extraTypes;
    
        transfer = null;
        extraTypes = new com.github.mzule.activityrouter.router.ExtraTypes();
        extraTypes.setTransfer(transfer);
        com.github.mzule.activityrouter.router.Routers.map("home/:productLine", ProductLineActivity.class, null, extraTypes);
         }
            }
       ```
       .map方法把我们的mapping放到集合中
       ```
        static void map(String format, Class<? extends Activity> activity, MethodInvoker method, ExtraTypes extraTypes) {
        mappings.add(new Mapping(format, activity, method, extraTypes));
         }
       ```
    2. 然后for循坏遍历这个集合找到我们需要的activity，创建intent，调用((Activity) context).startActivityForResult(intent, requestCode);
  * 总结：他的思想就是通过注解在编译期把我们需要的uri与我们的activity建立某种映射，启动的时候直接通过uri来查找相应的activity.
  *  为什么说这个框架不支持多种module的情况？
  在他的RouterProcessor类中？
  ```
   private void generateModulesRouterInit(String[] moduleNames) {
        MethodSpec.Builder initMethod = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        for (String module : moduleNames) {
            initMethod.addStatement("RouterMapping_" + module + ".map()");
        }
        TypeSpec routerInit = TypeSpec.classBuilder("RouterInit")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(initMethod.build())
                .build();
        try {
            JavaFile.builder("com.github.mzule.activityrouter.router", routerInit)
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  ```
  
  我们看到在每个module中能会生成
  JavaFile.builder("com.github.mzule.activityrouter.router", routerInit) RouterInit文件，他们包名都一样，这是不允许的，所以该框架在不支持多个module的情况，估计作者也在修复相关问题。有个最新此类框架的也叫ActivityRouter，思路大体一致。
  
---
几个问题：
* 我们在buidApk的时候是采取把所有的module都打包进去，然后根据后台获取的路由策略来实现不同的模块跳转显示，还是选择性的打包？
* 选择性打包如何实现？
    * gradle文件中我们可以配置相关的依赖完成选择性打包。
    * 在有些地方我们需要硬解码实现的地方该如何实现？就是有些地方非要用代码来实现相关功能。例如主页面模块，像这种，我们在后台获取策略的时候应该知道具体的跳转规则的，也就是这种uri映射我们是知道的。所以我们就可以根据这种规则页一套有规则的代码。
---
* 此类框架必备的知识：
* ==apt技术== 框架在多次用到，建议有人抽空能讲一下。
* gradle 自动化配置文件

[ARouter]https://baijiahao.baidu.com/po/feed/share?wfr=spider&for=pc&context=%7B%22sourceFrom%22%3A%22bjh%22%2C%22nid%22%3A%22news_3695090607611977894%22%7D)
* 目标实现：
从后台获取配置模块信息，动态构建我的app

* 动态体现在两个方面：
    1. 动态构建module
    2. 动态构建主界面

