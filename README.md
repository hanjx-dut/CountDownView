# CountDownView

倒计时 View，支持属性：

```xml
<declare-styleable name="CountDownView">
    <attr name="duration" format="integer"/>  <!-- 总时间, 默认 2000ms -->
    <attr name="refresh_interval">  <!-- 刷新间隔 ms -->
        <enum name="normal" value="16"/>
        <enum name="quick" value="11"/>
        <enum name="slow" value="20"/>
    </attr>
    <attr name="paintStroke" format="dimension"/>  <!-- 圆环宽度, 默认 3dp -->
    <attr name="finishedColor" format="color"/>  <!-- 扫过完成的颜色 -->
    <attr name="unfinishedColor" format="color"/>  <!-- 未完成的颜色 -->
    <attr name="start_angle" format="float"/>  <!-- 起始角度, 默认 -90 即顶部 -->
    <attr name="clockwise" format="boolean"/>  <!-- 顺时针, 默认 true -->
    <attr name="auto_start" format="boolean"/>  <!-- 自动开始, 默认 false -->
</declare-styleable>
```



效果

![img](https://github.com/hanjx-dut/CountDownView/blob/master/demo.gif)

