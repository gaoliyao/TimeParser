# TimeParser_newnew
## Future
|Template|Example|Code|
|--------|-------|----|
|[today]|today|10000|
|[tomorrow]|tmrw|10010|
|next + [weekdays]|next monday|10020|
|next + [day]|next day|10021|
|next + [week]|next day|10022|
|next + [month]|next day|10023|
|next + [year]|next year|10024|
|after|after|10030|
|after + [time]|after tmrw|10031|

### 10000
No operations

eg: Let's meet today.

### 10010
Add one day.
```java
day += 1;
```

eg: Let's meet tomorrow.

### 10020
