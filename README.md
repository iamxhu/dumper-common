dumper-common
=============

一个灵活的从数据库中dump数据，并生成指定格式文件的框架 

灵感来自于Solr的DIH，针对DIH中比较难理解的部分做了一些的简化。
主要功能：用于小数据量级（未做过100万数据量级的测试）情况下从数据库中Dump数据，支持全量和增量数据dump。
优点：
通过在dumper.xml文件中配置SQL，并配置相应的输出模板，完成指定格式的数据输出，配置非常灵活，更改输出字段时，只需要修改配置文件即可。
dumper.xml中配置的Entity是可嵌套，支持全局Cache，用户需要针对dump的字段进行修改时，可以实现Transformer接口定制自己的方法。
当前使用velocity模板做数据的输出，用户可定制自己的输出类，只需要实现FileWriter接口，并将其配置到dumper.xml文件中即可。

目前整个系统使用的是单线程处理，因此要dump非常大的数据量时，不宜采用该框架。
目前只支持MySQL数据库，针对MySQL的特性，在分页dump时做了相应的SQL优化，不会出现在Dump后面页面数据时，SQL查询太慢的问题。

当前的增量策略的默认实现是使用一个表来记录最近一次执行时间，因此需要使用增量时，需要创建如下表：
CREATE TABLE `dump_timer` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `lasttime` int(10) unsigned NOT NULL DEFAULT '0',
  `tbl` varchar(250) NOT NULL ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

插入记录时的tbl的值即是最外层的entity标签的name属性。
当然你也可以实现基于文件的增量策略，只需要实现IncrementSupport接口即可。
