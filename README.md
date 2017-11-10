# TimeParser
# TimeParser 
[TP](https://github.com/gaoliyao/TimeParser_old/blob/master/clock.png")

 According to research, users spend their time on social application in majority, receiving and texting messages. To understand the messages is absolutely important for the app developers. Time, as an essential concept in life, plays an important role in people's conversation. Nearly 30% of conversation have time component embedded in it. 
 

<img src="https://github.com/gaoliyao/TimeParser_old/blob/master/lyft-invite-friends-to-be-your-inspiration-in-designing-the-Party-invitation-card-so-it-looks-sensational-11.jpg" width="15%" height="15%">

Let's imagine a situation that if you would like to invite your good friend Jack to join your this party, you might text him: "Come and join the party this Saturday!". In this case, "this Saturday" is inferring to a time. 



However, this is a relative time which is inferred based on the date of the conversation. The developer might need to know the time for smart understanding services. Time Parser provides an easy access for developers to get the embeded time component in a sentence, which transform the phrases like "this Saturday" to "30 July, 2017". It's easy to use and is helpful to understand the messages.

# Examples:

Let's suppose that today is 28 July, 2017 18:00:00 (Thursday)

1. Can we meet this Friday?                         

(29 July, 2017 0:00:00 - 29 July, 2017 23:59:59)      
 
2. I'll be there in 20 minutes.                     

(28 July, 2017 18:20:00)                              
   
3. Can you send me the picture you took last Friday?
   
   (22 July, 2017 0:00:00 - 23:59:59)

# Sample Code:

```java
String sentence = "Let's meet at 5pm this Saturday!"
TimeParser tp = new TimeParser();
long[] recognizedTime = tp.GetInput(sentence);
//[1501776000943, 1504022399958]
```

## Future
|Template|Example|Code|
|--------|-------|----|
|[today]|today|10000|
|[tomorrow]|tmrw|10010|
|next + [day]|next day|10020|
|next + [weekdays]|next monday|10021|
|next + [week]|next week|10022|
|next + [month]|next month|10023|
|next + [year]|next year|10024|
|after|after|10030|
|after + [time]|after tmrw|10031|

## Past
|Template|Example|Code|
|--------|-------|----|
|[yesterday]|yesterday|20000|
|[tomorrow]|tmrw|10010|
|next + [day]|next day|10020|
|next + [weekdays]|next monday|10021|
|next + [week]|next week|10022|
|next + [month]|next month|10023|
|next + [year]|next year|10024|
|after|after|10030|
|after + [time]|after tmrw|10031|
