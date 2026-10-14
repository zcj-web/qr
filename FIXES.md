# 修复记录

## 问题1: 生成二维码失败 - QrDatabase_Impl 不存在
**原因**: 移除了 Hilt/KSP 后，Room 注解处理器未正确配置。
**修复**: 
- 添加 `id("kotlin-kapt")` 插件
- 将 `annotationProcessor` 改为 `kapt("androidx.room:room-compiler:2.6.1")`

## 问题2: 点击历史记录卡住
**原因**: HistoryActivity 尝试在 activity_history.xml 中查找 R.id.container，但该布局没有此 ID。
**修复**: 重写 HistoryActivity，直接在 Activity 中管理 RecyclerView，不再使用 Fragment。

## 问题3: 快速创建网格点击无反应
**原因**: quickGrid 是 LinearLayout（在 ScrollView 内），动态 addView 后触摸事件传递异常。
**修复**: 改用 RecyclerView + GridLayoutManager(4列) + 自定义 Adapter。

## 问题4: Navigation 组件卡死
**原因**: Fragment 直接放在 ViewPager2 中，没有 NavHostFragment，findNavController() 返回 null。
**修复**: 所有导航改为显式 Intent 跳转 + 独立 Activity 包装 Fragment。
