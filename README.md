YoutubeDL with AWS Flow Framework
------

Basic workflow to youtube video with AWS Flow Framework

Activities :

- get video information
- download video and convert video to host (youtube-dl and ffmpeg should be available in $PATH)
- upload to S3
- delete video on host

# Usage

This project use maven, you should have maven in your $PATH

`mvn package`

Launch workflow host

`java -cp target/yousync-swf-1.0.0.jar com.sapher.yousync.WorkflowHost`

Launch activity host

`java -cp target/yousync-swf-1.0.0.jar com.sapher.yousync.ActivityHost`

Launch a task

`java -cp target/yousync-swf-1.0.0.jar com.sapher.yousync.WorkflowExecutionStarter https://www.youtube.com/watch?v=luJJBeCFeM0`


Woo that doc suck!
