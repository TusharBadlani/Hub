package cessini.technology.commonui.common

import java.text.SimpleDateFormat
import java.util.*

fun Long.dateTime(): String =
    SimpleDateFormat(
        "EEE, MMM d, h:mm a",
        Locale.getDefault()
    ).format(this)

fun Long.dayTime(): String =
    SimpleDateFormat(
        "EEE·h:mma",
        Locale.getDefault()
    ).format(this)

fun Long.dayNTime(): String =
    SimpleDateFormat(
        "EEE\nh:mma",
        Locale.getDefault()
    ).format(this)
fun Long.MonthTime(): String =
    SimpleDateFormat(
        "MMM d h mm a",
        Locale.getDefault()
    ).format(this)
fun Long.YearTime():String=
    SimpleDateFormat(
        "yyyy MMM d h mm a",
        java.util.Locale.getDefault()
    ).format(this)

fun Long.compareDate( str:Long):Boolean
{
val s1 = this.YearTime().split(" ")
 val s2= str.YearTime().split(" ")
    if(s1[0]<s2[0])
        return false
    if(s1[1].month()<s2[1].month())
        return false
    else if(s1[1].month()>s2[1].month())
        return true
    else
    {
        with(s1[2])
        {
            if(this.toInt()>s2[2].toInt())
                return true
            else if(this.toInt()<s2[2].toInt())
                return false
            else
            {
                if(s1[5].equals("p.m.")&&s2[5].equals("a.m."))
                    return true
                else  if(s1[5].equals("a.m.")&&s2[5].equals("p.m."))
                    return true
                else
                {
                    if(Math.abs(s1[3].toInt()-s2[3].toInt())>2)
                        return false;
                }


            }
        }
        return true
    }

}
fun String.month():Int
{
   when(this)
   {
       "Jan" -> return 1
       "Feb" -> return 2
       "Mar" -> return 3
       "Apr" -> return 4
       "May" -> return 5
       "Jun" -> return 6
       "Jul" -> return 7
       "Aug" -> return 8
       "Sep" -> return 9
       "Oct" -> return 10
       "Nov" -> return 11
       "Dec" -> return 12

   }
    return -1
}
