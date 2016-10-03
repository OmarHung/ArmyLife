package hung.armylife.useless;

import android.util.Log;

/**
 * Created by Hung on 2016/5/27.
 */
public class Standard {
    public String[][] strNewVolunteerMaleStandard = {{"18:00","37","31"},{"18:25","36","30"},{"18:50","34","29"},{"19:15","32","28"}};
    public String[][] strNewVolunteerFemaleStandard = {{"20:50","22","21"},{"20:55","21","20"},{"21:20","19","19"},{"22:20","17","17"}};
    public String[][] strNewObligationStandard = {{"19:00","25","25"},{"19:20","24","24"},{"19:40","23","23"},{"20:00","22","22"}};
    public String[][] str3MonthVolunteerMaleStandard = {{"17:00","41","34"},{"17:25","40","33"},{"17:50","38","32"},{"18:15","36","31"}};
    public String[][] str3MonthVolunteerFemaleStandard = {{"19:50","26","24"},{"19:55","25","23"},{"20:20","23","23"},{"21:20","21","20"}};
    public String[][] str3MonthObligationStandard = {{"18:00","29","30"},{"18:20","27","28"},{"18:40","26","27"},{"19:00","25","25"}};
    public String[][] str4MonthVolunteerMaleStandard =   {{"","",""},{"","",""},{"","",""},{"","",""}};
    public String[][] str4MonthVolunteerFemaleStandard = {{"","",""},{"","",""},{"","",""},{"","",""}};
    public String[][] str4MonthObligationStandard =       {{"17:00","32","34"},{"17:20","30","32"},{"17:40","29","31"},{"18:10","28","29"}};
    public String[][] str5MonthVolunteerMaleStandard =   {{"","",""},{"","",""},{"","",""},{"","",""}};
    public String[][] str5MonthVolunteerFemaleStandard = {{"","",""},{"","",""},{"","",""},{"","",""}};
    public String[][] str5MonthObligationStandard = {{"16:00","35","38"},{"16:20","33","36"},{"16:40","32","35"},{"17:20","30","33"}};
    public String[][] strVolunteerMaleStandard =   {{"14:00","51","43"},{"14:25","50","42"},{"14:50","48","41"},{"15:15","46","40"},{"15:35","43","38"},{"16:00","40","36"},{"16:15","37","34"},{"16:25","33","31"},{"16:50","28","28"},{"17:20","24","24"},{"17:40","20","20"}};
    public String[][] strVolunteerFemaleStandard = {{"16:50","36","33"},{"16:55","35","32"},{"17:20","33","31"},{"18:20","30","29"},{"18:45","27","27"},{"19:00","24","24"},{"19:20","21","21"},{"19:30","19","19"},{"19:50","18","17"},{"20:25","17","14"},{"20:45","16","12"}};
    public String[][] strObligationStandard = {{"15:00","38","42"},{"15:20","36","40"},{"15:50","34","38"},{"16:30","32","36"}};
    public Standard() {

    }
    public String[] getItem(int sex, int age, int month, int type) {
        //Log.d("GetItem","Sex "+sex+" Age "+age+" Month "+month+" Type "+type);
        String[] item={"未提供","未提供","未提供"};
        if(sex==0) {
            if(month<3) {
                if(type==0) {
                    if(age>=19 && age<=22) {
                        item=strNewObligationStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=strNewObligationStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=strNewObligationStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=strNewObligationStandard[3];
                    }
                }else {
                    if(age>=19 && age<=22) {
                        item=strNewVolunteerMaleStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=strNewVolunteerMaleStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=strNewVolunteerMaleStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=strNewVolunteerMaleStandard[3];
                    }
                }
            }else if(month>=3 && month<4) {
                if(type==0) {
                    if(age>=19 && age<=22) {
                        item=str3MonthObligationStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=str3MonthObligationStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=str3MonthObligationStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=str3MonthObligationStandard[3];
                    }
                }else {
                    if(age>=19 && age<=22) {
                        item=str3MonthVolunteerMaleStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=str3MonthVolunteerMaleStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=str3MonthVolunteerMaleStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=str3MonthVolunteerMaleStandard[3];
                    }
                }
            }else if(month>=4 && month<5) {
                if(type==0) {
                    if(age>=19 && age<=22) {
                        item=str4MonthObligationStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=str4MonthObligationStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=str4MonthObligationStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=str4MonthObligationStandard[3];
                    }
                }else {
                    if(age>=19 && age<=22) {
                        item=str4MonthVolunteerMaleStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=str4MonthVolunteerMaleStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=str4MonthVolunteerMaleStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=str4MonthVolunteerMaleStandard[3];
                    }
                }
            }else if(month>=5 && month<6) {
                if(type==0) {
                    if(age>=19 && age<=22) {
                        item=str5MonthObligationStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=str5MonthObligationStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=str5MonthObligationStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=str5MonthObligationStandard[3];
                    }
                }else {
                    if(age>=19 && age<=22) {
                        item=str5MonthVolunteerMaleStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=str5MonthVolunteerMaleStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=str5MonthVolunteerMaleStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=str5MonthVolunteerMaleStandard[3];
                    }
                }
            }else if(month>=6) {
                if(type==0) {
                    if(age>=19 && age<=22) {
                        item=strObligationStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=strObligationStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=strObligationStandard[2];
                    }else if(age>=31) {
                        item=strObligationStandard[3];
                    }
                }else {
                    if(age>=19 && age<=22) {
                        item=strVolunteerMaleStandard[0];
                    }else if(age>=23 && age<=26) {
                        item=strVolunteerMaleStandard[1];
                    }else if(age>=27 && age<=30) {
                        item=strVolunteerMaleStandard[2];
                    }else if(age>=31 && age<=34) {
                        item=strVolunteerMaleStandard[3];
                    }else if(age>=35 && age<=38) {
                        item=strVolunteerMaleStandard[4];
                    }else if(age>=39 && age<=42) {
                        item=strVolunteerMaleStandard[5];
                    }else if(age>=43 && age<=46) {
                        item=strVolunteerMaleStandard[6];
                    }else if(age>=47 && age<=50) {
                        item=strVolunteerMaleStandard[7];
                    }else if(age>=51 && age<=54) {
                        item=strVolunteerMaleStandard[8];
                    }else if(age>=55 && age<=58) {
                        item=strVolunteerMaleStandard[9];
                    }else if(age>=59) {
                        item=strVolunteerMaleStandard[10];
                    }
                }
            }
        }else {

        }
        return item;
    }
}
