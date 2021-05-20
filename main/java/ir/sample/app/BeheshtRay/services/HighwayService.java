package ir.sample.app.highway.services;

import ir.appsan.sdk.APSService;
import ir.appsan.sdk.View;
import ir.appsan.sdk.ViewUpdate;
import ir.appsan.sdk.Response;
import ir.sample.app.highway.models.Owner;
import org.json.simple.JSONObject;

import java.sql.Connection;

public class HighwayService extends APSService {

    String selectedharf = "";
    String selectedtype = "";
    String selectedid = "";
    Owner owner = new Owner();
    Pelak pelak = new Pelak();
    Connection connection = DatabaseManager.getConnection();
    boolean allowmake = true;

    public HighwayService(String channelName) {
        super(channelName);
    }

    @Override
    public String getServiceName() {
        return "app:admin:highway";
    }

    @Override
    public View onCreateView(String command, JSONObject pageData, String userId) {
        View view;
        if (command.equals("addcar")) {
            return new AddCar();
        } else if (command.startsWith("idpelak")) {
            pelak = DbOperation.retrievePelak(command, connection);
            view = new HazineSee();
            view.setMustacheModel(pelak); //todo: empty mustache! ://
            return view;
        } else if (command.equals("pardakhts")) {
            return new NoPardakht();
        } else if (command.equals("rahnama")) {
            System.out.println("1");
            return new Rahnama();
        } else if (command.equals("deleteandseepelaks")) {
            DbOperation.deletepelak(selectedid, connection);
            owner.id = userId;
            owner.pelaks = DbOperation.retrievePelaks(userId, connection);
            if (owner.pelaks.size() == 0) {
                return new Enter();
            } else {
                view = new TarheTraffic();
                view.setMustacheModel(owner);
                return view;
            }
        } else if (command.equals("seepelaks")) {
            owner.id = userId;
            owner.pelaks = DbOperation.retrievePelaks(userId, connection);
            if (owner.pelaks.size() == 0) {
                return new Enter();
            } else {
                view = new TarheTraffic();
                view.setMustacheModel(owner);
                return view;
            }
        }
        owner.id = userId;
        owner.pelaks = DbOperation.retrievePelaks(userId, connection);
        if (owner.pelaks.size() == 0) {
            return new Enter();
        } else {
            view = new TarheTraffic();
            view.setMustacheModel(owner);
            return view;
        }
    }

    @Override
    public Response onUpdate(ViewUpdate update, String updateCommand, JSONObject pageData, String userId) {
        if (updateCommand.startsWith("doeditpelak")) {
            selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
            System.out.println(selectedid);
            EditCar view = new EditCar();
            pelak = DbOperation.retrievePelak(selectedid, connection);
            view.setMustacheModel(pelak);
            selectedtype = pelak.type;
            selectedharf = pelak.harf;
            return view;
        } else if (updateCommand.startsWith("opendeleteask")) {
            selectedid = updateCommand.substring(updateCommand.indexOf("+") + 1);
            return new Dialog3();
        } else if (updateCommand.equals("sabtecarcheck")) {
            String alert = "";
            String first = pageData.get("first").toString();
            if (first.length() != 2) {
                alert += "\r\nبخش اول پلاک را به صورت عدد دو رقمی وارد کنید";
                allowmake = false;
                update.addChildUpdate("first", "text", "");
            }
            String second = pageData.get("second").toString();
            if (second.length() != 3) {
                alert += "\r\nبخش دوم پلاک را به صورت عدد سه رقمی وارد کنید";
                allowmake = false;
                update.addChildUpdate("second", "text", "");
            }
            String third = pageData.get("third").toString();
            if (third.length() != 2) {
                alert += "\r\nبخش سوم پلاک را به صورت عدد دو رقمی وارد کنید";
                allowmake = false;
                update.addChildUpdate("third", "text", "");
            }
            String type = selectedtype;
            if (type.length() == 0) {
                alert += "\r\nحرف مربوط به پلاک را انتخاب کنید";
                allowmake = false;
            }
            String harf = selectedharf;
            if (harf.length() == 0) {
                alert += "\r\nنوع ماشین خود را وارد کنید";
                allowmake = false;
            }
            update.addChildUpdate("alert", "text", alert);
            if (allowmake) {
                Pelak pelak = new Pelak();
                pelak.first = pageData.get("first").toString();
                pelak.second = pageData.get("second").toString();
                pelak.third = pageData.get("third").toString();
                pelak.name = pageData.get("name").toString();
                pelak.type = selectedtype;
                pelak.harf = selectedharf;
                pelak.bedehi = 0;
                DbOperation.registerPelak(pelak, userId, connection);
                owner.id = userId;
                owner.pelaks = DbOperation.retrievePelaks(userId, connection);
                TarheTraffic view = new TarheTraffic();
                view.setMustacheModel(owner);
                allowmake = true;
                return view;
            } else {
                allowmake = true;
                return update;
            }
        } else if (updateCommand.equals("opentype")) {
            return new Dialog1();
        } else if (updateCommand.equals("openharf")) {
            return new Dialog2();
        } else if (updateCommand.equals("sabtecarcheckedit")) {
            String alert = "";
            String first = pageData.get("first").toString();
            if (first.length() != 2) {
                alert += "\r\nبخش اول پلاک را به صورت عدد دو رقمی وارد کنید";
                allowmake = false;
                update.addChildUpdate("first", "text", "");
            }
            String second = pageData.get("second").toString();
            if (second.length() != 3) {
                alert += "\r\nبخش دوم پلاک را به صورت عدد سه رقمی وارد کنید";
                allowmake = false;
                update.addChildUpdate("second", "text", "");
            }
            String third = pageData.get("third").toString();
            if (third.length() != 2) {
                alert += "\r\nبخش سوم پلاک را به صورت عدد دو رقمی وارد کنید";
                allowmake = false;
                update.addChildUpdate("third", "text", "");
            }
            String type = selectedtype;
            if (type.length() == 0) {
                alert += "\r\nحرف مربوط به پلاک را انتخاب کنید";
                allowmake = false;
            }
            String harf = selectedharf;
            if (harf.length() == 0) {
                alert += "\r\nنوع ماشین خود را وارد کنید";
                allowmake = false;
            }
            update.addChildUpdate("alert", "text", alert);
            if (allowmake) {
                Pelak pelak = new Pelak();
                pelak.first = pageData.get("first").toString();
                pelak.second = pageData.get("second").toString();
                pelak.third = pageData.get("third").toString();
                pelak.name = pageData.get("name").toString();
                pelak.type = selectedtype;
                pelak.harf = selectedharf;
                pelak.id = selectedid;
                DbOperation.editpelak(pelak, connection);
                owner.id = userId;
                owner.pelaks = DbOperation.retrievePelaks(userId, connection);
                TarheTraffic view = new TarheTraffic();
                view.setMustacheModel(owner);
                allowmake = true;
                return view;
            } else {
                allowmake = true;
                return update;
            }
        } else if (updateCommand.equals("الف")) {
            selectedharf = "الف";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ب")) {
            selectedharf = "ب";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("پ")) {
            selectedharf = "پ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ت")) {
            selectedharf = "ت";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ث")) {
            selectedharf = "ث";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ج")) {
            selectedharf = "ج";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("چ")) {
            selectedharf = "چ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ح")) {
            selectedharf = "ح";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("خ")) {
            selectedharf = "خ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("د")) {
            selectedharf = "د";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ذ")) {
            selectedharf = "ذ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ر")) {
            selectedharf = "ر";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ز")) {
            selectedharf = "ز";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ژ")) {
            selectedharf = "ژ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("س")) {
            selectedharf = "س";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ش")) {
            selectedharf = "ش";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ص")) {
            selectedharf = "ص";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ض")) {
            selectedharf = "ض";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ط")) {
            selectedharf = "ط";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ظ")) {
            selectedharf = "ظ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ع")) {
            selectedharf = "ع";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("غ")) {
            selectedharf = "غ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ف")) {
            selectedharf = "ف";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ق")) {
            selectedharf = "ق";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ک")) {
            selectedharf = "ک";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("گ")) {
            selectedharf = "گ";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ل")) {
            selectedharf = "ل";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("م")) {
            selectedharf = "م";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ن")) {
            selectedharf = "ن";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("و")) {
            selectedharf = "و";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ه")) {
            selectedharf = "ه";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("ی")) {
            selectedharf = "ی";
            update.addChildUpdate("harfhere", "text", selectedharf);
        } else if (updateCommand.equals("سواری")) {
            selectedtype = "سواری";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("وانت و مینی بوس")) {
            selectedtype = "وانت و مینی بوس";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("کامیونت")) {
            selectedtype = "کامیونت";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("اتوبوس و کامیون دو محور")) {
            selectedtype = "اتوبوس و کامیون دو محور";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("کامیون سه محور")) {
            selectedtype = "کامیون سه محور";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("تریلی")) {
            selectedtype = "تریلی";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("تانکر و نفت کش")) {
            selectedtype = "تانکر و نفت کش";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("سواری عمومی")) {
            selectedtype = "سواری عمومی";
            update.addChildUpdate("typehere", "text", selectedtype);
        } else if (updateCommand.equals("سواری بومی")) {
            selectedtype = "سواری بومی";
            update.addChildUpdate("typehere", "text", selectedtype);
        }
        return update;
    }
}
