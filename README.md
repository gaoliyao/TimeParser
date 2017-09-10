# TimeParser_newnew
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

### 10000
No operations.

Eg: Let's meet today.

### 10010
Add one day.
```java
day += 1;
```

Eg: Let's meet tomorrow.

### 10020
Add one day. 
```java
day += 1;
```

Eg: Let's meet on next day. 

### 10021
Add the days between.
```java
int diff = weekdayInSentence - weekdayCurrent;
if(diff > 0){
    day += diff + 7;
    }
else{
    day += 7 - diff;
}
```

Eg: Let's meet on next Friday. 


### 10022
Add one week. 
```java
day += 7;
```

Eg: See u next week. 

### 10023
Add one month. (Need to be fixed)
```java
day += 30;
```

Eg: Let's meet on next month. 

### 10024
Add one year. (Need to be fixed)
```java
day += 365;
```

Eg: The meeting will be helded in the next year. 
