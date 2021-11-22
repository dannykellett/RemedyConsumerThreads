# Remedy Consumer Threads

Created to answer [Regarding multi threaded Java plugin programming](https://community.bmc.com/s/question/0D53n00007aEalACAS/regarding-multi-threaded-java-plugin-programming) question on the BMC Communities forum. This is an example of using the BMC Remedy API to populate a thread safe BlockingQueue<Entry> list to be processed by a set of instantiated threads.

It's important to understand the following:
- The list is created outside and before the consumer threads are created
- The consumer threads do not wait for each other to complete. Which is fine if your consumer threads do all the necessary work. But the code needs to do something else once the consumer threads are complete, then this is not the best method (I may do another example with [Executors](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html) if required.
- I use [Gradle](https://gradle.org/) instead of Maven but can easily be ported

https://dannykellett.com

## Installation

- Create a Regular form called DummyForm
- Use GenerateData.class to generate 10000 test entries
- Run the App.main

Example run showing thread names processing different entries and completing 10000 updates in 10s
```bash
2020-10-12 17:25:29:324 [INFO ] [GenerateData        :92   ] - Remedy Consumer Threads v1.0 - https://dannykellett.com
2020-10-12 17:25:29:929 [INFO ] [GenerateData        :94   ] - 10000 Entries found to update
2020-10-12 17:25:30:070 [INFO ] [PluginsmiUpdate     :25   ] - Thread-3 Updating entry 000000000021924
2020-10-12 17:25:30:071 [INFO ] [PluginsmiUpdate     :25   ] - Thread-7 Updating entry 000000000021928
2020-10-12 17:25:30:071 [INFO ] [PluginsmiUpdate     :25   ] - Thread-10 Updating entry 000000000021931
2020-10-12 17:25:30:071 [INFO ] [PluginsmiUpdate     :25   ] - Thread-9 Updating entry 000000000021930
2020-10-12 17:25:30:071 [INFO ] [PluginsmiUpdate     :25   ] - Thread-8 Updating entry 000000000021929
2020-10-12 17:25:30:071 [INFO ] [PluginsmiUpdate     :25   ] - Thread-6 Updating entry 000000000021927
2020-10-12 17:25:30:070 [INFO ] [PluginsmiUpdate     :25   ] - Thread-5 Updating entry 000000000021926
2020-10-12 17:25:30:070 [INFO ] [PluginsmiUpdate     :25   ] - Thread-4 Updating entry 000000000021925
...
2020-10-12 17:25:39:643
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
