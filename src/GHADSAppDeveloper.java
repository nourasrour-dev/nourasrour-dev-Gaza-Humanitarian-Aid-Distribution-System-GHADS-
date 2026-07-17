package gaza.aid.tracker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * نظام GHADS Pro - نسخة مطورة ومبسطة لقطاع غزة
 * تم إصلاح معالج الحفظ، وإضافة جدار الصلاحيات الحصري للمنظمة والمتطوع.
 */
public class GHADSAppDeveloper extends Application {

    private static final int INITIAL_STOCK = 5000;
    private ArrayList<AidDistribution> distributionList = new ArrayList<>();
    
    private final Organization unicef = new Organization(1, "UNICEF_Gaza", "123", "Organization", "Central Area");
    private final Volunteer fieldVolunteer = new Volunteer(2, "Ahmad_Volunteer", "123", "Central Area");
    
    private final ObservableList<User> systemUsers = FXCollections.observableArrayList();

    // لوحة الألوان الفاتحة الاحترافية المنسقة بالكامل
    private final String sidebarBg = "#F1F5F9";        
    private final String sidebarActiveBg = "#E2E8F0";  
    private final String sidebarText = "#475569";      
    private final String sidebarActiveText = "#4F46E5";  
    private final String purple = "#4F46E5";           
    private final String yellow = "#D97706";           
    private final String green = "#059669";            
    private final String pink = "#E11D48";             
    private final String uiBg = "#F8FAFC";             
    private final String uiCardBg = "#FFFFFF";         
    private final String uiBorder = "#E2E8F0";         
    private final String textDark = "#0F172A";         
    private final String textMuted = "#64748B";        
    private final String fontStyle = "-fx-font-family: 'Segoe UI', 'Arial', sans-serif; ";

    private Stage primaryStage;
    private String currentRole = "UNICEF_Gaza"; 
    private String currentPage = "aid";       
    private boolean loggedIn = true;

    private BorderPane root;
    private VBox sidebar;
    private StackPane contentArea;
    private Label statusLabel = new Label(" حالة النظام: نشط. تم تعيين كافة كلمات المرور إلى الكلمة الافتراضية الموحدة (123).");
    private Label sidebarNameLbl = new Label();
    private Label sidebarRoleLbl = new Label();

    private final LinkedHashMap<String, Button> navButtons = new LinkedHashMap<>();

    // إحصائيات لوحة التحكم
    private final Label statLabel1 = new Label("0"), statLabel2 = new Label("5000"),
            statLabel3 = new Label("0"), statLabel4 = new Label("5000");

    // صفحة سجلات المساعدات
    private final ObservableList<AidDistribution> aidData = FXCollections.observableArrayList();
    private TableView<AidDistribution> aidTable;
    private TextField fldDistId, fldFamilyName, fldMembers, fldContact, fldQty, fldLastAidDate, searchField;
    private Label fldFamilyIdPreview;
    private CheckBox chkDisplaced;
    private Button btnRecord, btnReset, btnRefresh, btnDelete;
    private String editingDistId = null; 

    // صفحة العائلات
    private final ObservableList<Family> familiesData = FXCollections.observableArrayList();

    public static void main(String[] args) {
        Application.launch(GHADSAppDeveloper.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("GHADS Pro - نظام تتبع المساعدات وإدارة المستخدمين");
        
        unicef.setPassword("123");
        fieldVolunteer.setPassword("123");
        systemUsers.addAll(unicef, fieldVolunteer);
        
        // تحميل البيانات المحفوظة مسبقاً عند تشغيل التطبيق
        distributionList = DatabaseManager.loadFromFile(unicef);

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + uiBg + ";");
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT); 

        sidebar = buildSidebar();
        root.setRight(sidebar); 

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(15, 20, 15, 20)); 
        BorderPane.setAlignment(contentArea, Pos.TOP_CENTER);
        root.setCenter(contentArea);

        statusLabel.setStyle(fontStyle + "-fx-font-size: 12px; -fx-text-fill: " + textMuted + "; -fx-padding: 4 15;");
        HBox footer = new HBox(statusLabel);
        footer.setStyle("-fx-background-color: " + uiCardBg + "; -fx-border-color: " + uiBorder + " transparent transparent transparent; -fx-border-width: 1px;");
        root.setBottom(footer);

        switchPage(currentPage);

        Scene scene = new Scene(root, 1240, 780);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox buildSidebar() {
        VBox bar = new VBox(4);
        bar.setPrefWidth(230);
        bar.setMinWidth(230);
        bar.setStyle("-fx-background-color: " + sidebarBg + "; -fx-border-color: transparent " + uiBorder + " transparent transparent; -fx-border-width: 1px;");
        bar.setPadding(new Insets(20, 12, 20, 12));

        StackPane avatar = new StackPane();
        Circle circle = new Circle(30, Color.web(purple));
        Label avatarLbl = new Label("👨‍💼");
        avatarLbl.setStyle("-fx-font-size: 24px;");
        avatar.getChildren().addAll(circle, avatarLbl);

        sidebarNameLbl.setStyle(fontStyle + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + textDark + ";");
        sidebarRoleLbl.setStyle(fontStyle + "-fx-font-size: 11px; -fx-text-fill: " + purple + "; -fx-font-weight: bold;");

        VBox profileBox = new VBox(4, avatar, sidebarNameLbl, sidebarRoleLbl);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(5, 0, 15, 0));
        updateSidebarProfile();

        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.15; -fx-padding: 0 0 10 0;");

        VBox navBox = new VBox(5);
        navBox.getChildren().addAll(
                navBtn("dashboard", "🏠    لوحة التحكم الإحصائية"),
                navBtn("users", "👥    إدارة صلاحيات المستخدمين"),
                navBtn("orgs", "🏢    الملف التعريفي للمنظمة"),
                navBtn("families", "👪    قاعدة بيانات العائلات"),
                navBtn("aid", "📦    سجل توزيع المساعدات"),
                navBtn("profile", "🪪    تحديث الملف الشخصي"),
                navBtn("password", "🔒    إدارة كلمة المرور")
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪    تسجيل الخروج الآمن");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setAlignment(Pos.CENTER_RIGHT);
        logoutBtn.setStyle(fontStyle + "-fx-background-color: transparent; -fx-text-fill:" + pink
                + "; -fx-font-weight: bold; -fx-padding: 10 14; -fx-background-radius: 8; -fx-cursor: hand;"
                + "-fx-border-color:" + pink + "; -fx-border-radius: 8; -fx-border-width: 1px;");
        logoutBtn.setOnAction(e -> doLogout());

        bar.getChildren().addAll(profileBox, sep, navBox, spacer, logoutBtn);
        return bar;
    }

    private Button navBtn(String key, String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_RIGHT);
        b.setOnAction(e -> switchPage(key));
        navButtons.put(key, b);
        return b;
    }

    private void updateNavStyles() {
        for (Map.Entry<String, Button> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(currentPage);
            String style = fontStyle + "-fx-padding: 10 14; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px;";
            if (active) {
                style += "-fx-background-color:" + sidebarActiveBg + "; -fx-text-fill:" + sidebarActiveText
                        + "; -fx-font-weight: bold; -fx-border-color: " + sidebarActiveText + "; -fx-border-width: 0 4 0 0;";
            } else {
                style += "-fx-background-color: transparent; -fx-text-fill:" + sidebarText + "; -fx-font-weight: normal;";
            }
            entry.getValue().setStyle(style);
        }
    }

    private void updateSidebarProfile() {
        User u = accountForRole(currentRole);
        if (u != null) {
            sidebarNameLbl.setText(u.getUser_Name());
            sidebarRoleLbl.setText(u.getUser_Type().equals("Organization") ? "حساب المنظمة (مدير النظام)" : "حساب المتطوع الميداني");
        }
    }

    private void switchPage(String key) {
        if (!loggedIn) return;
        currentPage = key;
        updateNavStyles();
        
        switch (key) {
            case "dashboard":
                contentArea.getChildren().setAll(buildDashboardPage());
                break;
            case "users":
                contentArea.getChildren().setAll(buildUsersPage());
                break;
            case "orgs":
                contentArea.getChildren().setAll(buildOrganizationsPage());
                break;
            case "families":
                refreshFamiliesData();
                contentArea.getChildren().setAll(buildFamiliesPage());
                break;
            case "aid":
                contentArea.getChildren().setAll(buildAidRecordsPage());
                break;
            case "profile":
                contentArea.getChildren().setAll(buildProfilePage());
                break;
            case "password":
                contentArea.getChildren().setAll(buildChangePasswordPage());
                break;
            default:
                contentArea.getChildren().setAll(buildAidRecordsPage());
        }
    }

    private User accountForRole(String roleName) {
        for (User u : systemUsers) {
            if (u.getUser_Name().equalsIgnoreCase(roleName)) {
                return u;
            }
        }
        return systemUsers.isEmpty() ? unicef : systemUsers.get(0);
    }

    private boolean promptPasswordDialog(String targetRoleName, Stage owner) {
        User targetUser = accountForRole(targetRoleName);
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("تأكيد هوية الدخول للنظام");
        dialog.setResizable(false);
        
        VBox dialogRoot = new VBox(12);
        dialogRoot.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        dialogRoot.setPadding(new Insets(20));
        dialogRoot.setAlignment(Pos.CENTER_RIGHT);
        dialogRoot.setStyle("-fx-background-color: " + uiCardBg + "; -fx-background-radius: 12;");
        
        Label promptLabel = new Label("الرجاء إدخال كلمة مرور الحساب المصرح له: " + targetUser.getUser_Name());
        promptLabel.setStyle(fontStyle + "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + textDark + ";");
        
        PasswordField pf = new PasswordField();
        pf.setPromptText("كلمة المرور الافتراضية هي: 123");
        pf.setStyle(fontStyle + "-fx-padding: 8; -fx-background-radius: 6; -fx-border-color: " + uiBorder + "; -fx-border-radius: 6;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle(fontStyle + "-fx-text-fill: " + pink + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        final boolean[] success = {false};
        
        Button btnConfirm = new Button("تأكيد وتخويل الدخول");
        styleButton(btnConfirm, purple);
        btnConfirm.setMaxWidth(Double.MAX_VALUE);
        btnConfirm.setOnAction(e -> {
            String inputPass = pf.getText();
            if ("123".equals(inputPass) || targetUser.getPassword().equals(inputPass)) {
                success[0] = true;
                dialog.close();
            } else {
                errorLabel.setText("❌ كلمة المرور غير صحيحة!");
            }
        });
        
        dialogRoot.getChildren().addAll(promptLabel, pf, errorLabel, btnConfirm);
        
        Scene scene = new Scene(dialogRoot, 360, 180);
        dialog.setScene(scene);
        dialog.showAndWait();
        
        return success[0];
    }

    private void doLogout() {
        loggedIn = false;
        showModernAlert(Alert.AlertType.INFORMATION, "تسجيل الخروج", "تم قفل الجلسة الحالية بنجاح.");
        Platform.exit();
    }

    private VBox buildChangePasswordPage() {
        VBox page = pageWrapper("إدارة كلمة المرور", "إعدادات الأمان الخاصة بحماية حسابك وتغيير رموز الدخول.");
        VBox card = card();
        card.setMaxWidth(600);
        
        Label title = new Label("🔒 تعديل كلمة المرور المعتمدة");
        title.setStyle(fontStyle + "-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:" + purple + ";");

        User u = accountForRole(currentRole);

        PasswordField pfCurrent = new PasswordField();
        pfCurrent.setPromptText("اكتب كلمة المرور الحالية للتحقق (الافتراضية: 123)");
        pfCurrent.setStyle(fontStyle + "-fx-padding:10; -fx-background-radius:8; -fx-border-color:" + uiBorder + "; -fx-border-radius:8;");

        PasswordField pfNew = new PasswordField();
        pfNew.setPromptText("اكتب كلمة المرور الجديدة المراد تعيينها");
        pfNew.setStyle(fontStyle + "-fx-padding:10; -fx-background-radius:8; -fx-border-color:" + uiBorder + "; -fx-border-radius:8;");

        Button btnSave = new Button("حفظ وتحديث كلمة المرور الجديدة");
        styleButton(btnSave, purple);
        btnSave.setOnAction(e -> {
            String currentInput = pfCurrent.getText().trim();
            String newPass = pfNew.getText().trim();

            if (currentInput.isEmpty() || newPass.isEmpty()) {
                showModernAlert(Alert.AlertType.ERROR, "خانات فارغة", "يرجى ملء خانة كلمة المرور الحالية والجديدة معاً.");
                return;
            }

            if ("123".equals(currentInput) || u.getPassword().equals(currentInput)) {
                u.setPassword(newPass);
                showModernAlert(Alert.AlertType.INFORMATION, "تم تحديث الأمان", "تمت ترقية كلمة مرور الحساب بنجاح.");
                pfCurrent.clear();
                pfNew.clear();
            } else {
                showModernAlert(Alert.AlertType.ERROR, "فشل التحقق", "كلمة المرور الحالية التي أدخلتها غير صحيحة، يرجى إعادة المحاولة.");
            }
        });

        card.getChildren().addAll(title, pfCurrent, pfNew, btnSave);
        page.getChildren().add(card);
        return page;
    }

    private VBox buildUsersPage() {
        VBox page = pageWrapper("إدارة الصلاحيات والمستخدمين النشطين", "إدارة المستخدمين وضبط مستويات الأمان والتحكم الميداني.");

        boolean isOrg = accountForRole(currentRole).getUser_Type().equals("Organization");

        // كارت إضافة متطوع جديد (صلاحية حصرية للمنظمة فقط!)
        VBox addVolunteerCard = card();
        if (isOrg) {
            Label addTitle = new Label("➕ تسجيل متطوع جديد بالنظام");
            addTitle.setStyle(fontStyle + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + purple + ";");

            TextField vIdField = createField("الرقم التعريفي الفريد للمتطوع (رقم فقط)");
            TextField vNameField = createField("اسم المستخدم للدخول (مثال: noura)");
            TextField vAreaField = createField("منطقة التغطية الجغرافية المسؤولة (مثال: Rafah)");

            Button btnAddVolunteer = new Button("تسجيل المتطوع وتفويض الصلاحية الميدانية");
            styleButton(btnAddVolunteer, purple);
            btnAddVolunteer.setOnAction(e -> {
                try {
                    int id = Integer.parseInt(vIdField.getText().trim());
                    String name = vNameField.getText().trim();
                    String area = vAreaField.getText().trim();

                    if (name.isEmpty() || area.isEmpty()) throw new IllegalArgumentException();
                    
                    for (User u : systemUsers) {
                        if (u.getUser_Name().equalsIgnoreCase(name)) {
                            showModernAlert(Alert.AlertType.ERROR, "اسم المستخدم مسجل مسبقاً", "عذراً، اسم المستخدم متوفر لشخص آخر في النظام.");
                            return;
                        }
                    }

                    Volunteer newVol = new Volunteer(id, name, "123", area);
                    systemUsers.add(newVol);

                    showModernAlert(Alert.AlertType.INFORMATION, "تم تسجيل المتطوع بنجاح", "تم تسجيل الحساب الجديد بنجاح بكلمة مرور 123.");
                    vIdField.clear(); vNameField.clear(); vAreaField.clear();
                    switchPage("users");
                } catch (Exception ex) {
                    showModernAlert(Alert.AlertType.ERROR, "فشل التسجيل", "يرجى ملء كافة البيانات بصورة صحيحة وبأرقام صالحة للهوية.");
                }
            });
            addVolunteerCard.getChildren().addAll(addTitle, vIdField, vNameField, vAreaField, btnAddVolunteer);
        } else {
            // واجهة المتطوع: تعطيل وعرض رسالة قفل الصلاحية لجمالية واجهة المستخدم
            Label lockTitle = new Label("🔒 قسم إضافة متطوعين مغلق");
            lockTitle.setStyle(fontStyle + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + pink + ";");
            Label lockDesc = new Label("تسجيل المتطوعين الجدد هي صلاحية حصرية لمدراء النظام في المنظمة الشريكة فقط.");
            lockDesc.setStyle(fontStyle + "-fx-font-size: 13px; -fx-text-fill:" + textMuted + ";");
            addVolunteerCard.getChildren().addAll(lockTitle, lockDesc);
        }

        VBox listCard = card();
        Label listTitle = new Label("👥 جلسات المستخدمين المصرح لهم بالدخول");
        listTitle.setStyle(fontStyle + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + purple + ";");
        listCard.getChildren().add(listTitle);

        for (User u : systemUsers) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_RIGHT);
            row.setStyle("-fx-padding: 12; -fx-background-color: #F8FAFC; -fx-background-radius: 10; -fx-border-color:" + uiBorder + "; -fx-border-radius: 10; -fx-border-width: 1px;");

            VBox info = new VBox(4);
            Label nameLbl = new Label("اسم المستخدم: " + u.getUser_Name() + "  (#" + u.getUser_ID() + ")");
            nameLbl.setStyle(fontStyle + "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill:#0F172A;");
            
            Label typeLbl = new Label("نوع الحساب: " + (u.getUser_Type().equals("Organization") ? "منظمة شريكة" : "متطوع ميداني") + " | كلمة المرور المعتمدة: 123");
            typeLbl.setStyle(fontStyle + "-fx-font-size: 12px; -fx-text-fill:" + textMuted + ";");
            
            Label permLbl = new Label("نطاق الصلاحية المخولة: " + u.getRolePermissions());
            permLbl.setWrapText(true);
            permLbl.setMaxWidth(550);
            permLbl.setStyle(fontStyle + "-fx-font-size: 11px; -fx-text-fill:#475569;");
            info.getChildren().addAll(nameLbl, typeLbl, permLbl);
            HBox.setHgrow(info, Priority.ALWAYS);

            String roleKey = u.getUser_Name();
            boolean isCurrentActive = roleKey.equalsIgnoreCase(currentRole);
            Button switchBtn = new Button(isCurrentActive ? "✅ الجلسة النشطة" : "تبديل الحساب");
            switchBtn.setDisable(isCurrentActive);
            styleButton(switchBtn, isCurrentActive ? "#64748B" : purple);
            switchBtn.setMinWidth(140);
            switchBtn.setMaxWidth(140);
            switchBtn.setOnAction(e -> {
                if (promptPasswordDialog(roleKey, primaryStage)) {
                    currentRole = roleKey;
                    updateSidebarProfile();
                    applyRolePermissionsToAidPage();
                    switchPage("users");
                }
            });

            row.getChildren().addAll(switchBtn, info);
            listCard.getChildren().add(row);
        }

        page.getChildren().addAll(addVolunteerCard, listCard);
        return page;
    }

    private VBox buildDashboardPage() {
        recomputeStats();
        VBox page = pageWrapper("لوحة التحكم والمؤشرات", "بيانات رصد العمليات الميدانية وسجل الاستهلاك اللحظي.");

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15); statsGrid.setVgap(15);
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(25);
        statsGrid.getColumnConstraints().addAll(cc, cc, cc, cc);

        statsGrid.add(buildStatCard("إجمالي العائلات المستفيدة 📋", statLabel1, "#0284C7", "#F0F9FF"), 3, 0);
        statsGrid.add(buildStatCard("المخزون الافتتاحي الكلي 📦", statLabel2, "#0D9488", "#F0FDFA"), 2, 0);
        statsGrid.add(buildStatCard("الوحدات الموزعة فعلياً 📊", statLabel3, "#B45309", "#FFFBEB"), 1, 0);
        statsGrid.add(buildStatCard("الرصيد المتاح بالمخزن 💰", statLabel4, pink, "#FFF1F2"), 0, 0);

        VBox recentCard = card();
        Label recentTitle = new Label("آخر سجلات التوزيع المسجلة");
        recentTitle.setStyle(fontStyle + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + purple + ";");
        VBox recentList = new VBox(8);
        int shown = 0;
        for (int i = distributionList.size() - 1; i >= 0 && shown < 5; i--, shown++) {
            AidDistribution d = distributionList.get(i);
            Label row = new Label("• إيصال " + d.getDistribution_ID() + " — العائلة المستلمة: " + d.getReceiving_Family().getFamily_Name() + " (" + d.getDistributed_Quantity() + " وحدة)");
            row.setStyle(fontStyle + "-fx-font-size: 13px; -fx-text-fill: #334155;");
            recentList.getChildren().add(row);
        }
        if (distributionList.isEmpty()) {
            Label empty = new Label("لا توجد عمليات توزيع مدرجة حتى اللحظة.");
            empty.setStyle(fontStyle + "-fx-font-size: 13px; -fx-text-fill:" + textMuted + ";");
            recentList.getChildren().add(empty);
        }
        recentCard.getChildren().addAll(recentTitle, recentList);

        page.getChildren().addAll(statsGrid, recentCard);
        return page;
    }

    private void recomputeStats() {
        Set<Integer> uniqueFamilyIds = new HashSet<>();
        for (AidDistribution dist : distributionList) uniqueFamilyIds.add(dist.getReceiving_Family().getFamily_ID());
        statLabel1.setText(String.valueOf(uniqueFamilyIds.size()));
        statLabel2.setText(String.valueOf(INITIAL_STOCK));
        int totalReleased = getTotalDeliveredUnits();
        statLabel3.setText(String.valueOf(totalReleased));
        statLabel4.setText(String.valueOf(INITIAL_STOCK - totalReleased));
    }

    private int getTotalDeliveredUnits() {
        int sum = 0;
        for (AidDistribution d : distributionList) sum += d.getDistributed_Quantity();
        return sum;
    }

    private VBox buildStatCard(String title, Label valueLabel, String colorHex, String bgHex) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: " + bgHex + "; -fx-border-color: " + colorHex + "; -fx-border-width: 1px; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 15;");
        card.setAlignment(Pos.CENTER_RIGHT);
        
        Label tLabel = new Label(title);
        tLabel.setStyle(fontStyle + "-fx-font-size: 12px; -fx-text-fill: " + textMuted + "; -fx-font-weight: bold;");
        
        valueLabel.setStyle(fontStyle + "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        
        card.getChildren().addAll(tLabel, valueLabel);
        return card;
    }

    // =========================================================================
    // واجهة سجلات المساعدات الأساسية
    // =========================================================================
    private VBox buildAidRecordsPage() {
        VBox page = pageWrapper("إدخل وسجلات المساعدات", "إدارة وتوزيع الحصص التموينية على العائلات بالتكامل مع شروط الصلاحيات.");
        page.setSpacing(10); 

        VBox formCard = card();
        formCard.setPadding(new Insets(12, 18, 12, 18));
        Label formTitle = new Label("بيانات التوزيع والأسرة");
        formTitle.setStyle(fontStyle + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill:" + purple + ";");

        fldDistId = createField("رقم إيصال التوزيع (مثال: REC-01)");
        fldFamilyName = createField("اسم رب الأسرة الكامل");
        fldMembers = createField("عدد أفراد الأسرة");
        fldContact = createField("رقم هاتف التواصل");
        fldLastAidDate = createField("تاريخ آخر مساعدة (أو اكتب: لم يتلقَ)");
        fldLastAidDate.setText("لم يتلقَ");
        fldQty = createField("الكمية الموزعة حالياً (بالوحدات)");
        
        chkDisplaced = new CheckBox("العائلة المستفيدة نازحة حالياً؟");
        chkDisplaced.setStyle(fontStyle + "-fx-node-orientation: right-to-left; -fx-text-fill: " + textDark + ";");

        fldFamilyIdPreview = new Label("سيتم توليد رقم عائلي فريد تلقائياً عند تأكيد الحفظ");
        fldFamilyIdPreview.setStyle(fontStyle + "-fx-font-size: 11px; -fx-text-fill:" + textMuted + ";");

        GridPane gridFields = new GridPane();
        gridFields.setHgap(15);
        gridFields.setVgap(10);
        
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        gridFields.getColumnConstraints().addAll(col1, col2);

        gridFields.add(fldDistId, 1, 0);
        gridFields.add(fldFamilyName, 0, 0);
        gridFields.add(fldMembers, 1, 1);
        gridFields.add(fldContact, 0, 1);
        gridFields.add(fldLastAidDate, 1, 2);
        gridFields.add(fldQty, 0, 2);

        HBox optionsRow = new HBox(30, chkDisplaced, fldFamilyIdPreview);
        optionsRow.setAlignment(Pos.CENTER_RIGHT);
        optionsRow.setPadding(new Insets(4, 0, 0, 0));

        formCard.getChildren().addAll(formTitle, gridFields, optionsRow);

        VBox actionsCard = card();
        actionsCard.setPadding(new Insets(12, 18, 12, 18));
        Label actionsTitle = new Label("العمليات المتاحة");
        actionsTitle.setStyle(fontStyle + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill:" + purple + ";");

        btnRecord = new Button("💾 تسجيل وحفظ");
        styleButton(btnRecord, purple);
        btnRecord.setOnAction(e -> handleRecordDistribution());
        
        btnReset = new Button("🧹 تطهير الخانات");
        styleButton(btnReset, yellow);
        btnReset.setOnAction(e -> clearAidForm());
        
        btnRefresh = new Button("🔄 مزامنة وتحديث");
        styleButton(btnRefresh, green);
        btnRefresh.setOnAction(e -> refreshAidTable());
        
        btnDelete = new Button("🗑️ إلغاء وحذف");
        styleButton(btnDelete, pink);
        btnDelete.setOnAction(e -> handleDeleteDistribution());

        HBox actionRow1 = new HBox(12, btnRecord, btnReset);
        actionRow1.setAlignment(Pos.CENTER);
        HBox.setHgrow(btnRecord, Priority.ALWAYS); HBox.setHgrow(btnReset, Priority.ALWAYS);

        HBox actionRow2 = new HBox(12, btnRefresh, btnDelete);
        actionRow2.setAlignment(Pos.CENTER);
        HBox.setHgrow(btnRefresh, Priority.ALWAYS); HBox.setHgrow(btnDelete, Priority.ALWAYS);

        actionsCard.getChildren().addAll(actionsTitle, actionRow1, actionRow2);

        HBox topRow = new HBox(20, formCard, actionsCard);
        HBox.setHgrow(formCard, Priority.ALWAYS);

        VBox tableCard = card();
        tableCard.setPadding(new Insets(12, 18, 12, 18));
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        
        Label tableTitle = new Label("سجلات جدول التوزيع التفصيلي");
        tableTitle.setStyle(fontStyle + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill:" + purple + ";");

        searchField = createField("ابحث باسم رب الأسرة المباشر...");
        Button searchBtn = new Button("بحث سريع");
        styleButton(searchBtn, purple); searchBtn.setMaxWidth(120);
        searchBtn.setOnAction(e -> doSearch());
        
        Button showAllBtn = new Button("عرض الكل");
        styleButton(showAllBtn, "#64748B"); showAllBtn.setMaxWidth(120);
        showAllBtn.setOnAction(e -> refreshAidTable());

        HBox searchBar = new HBox(10, searchField, searchBtn, showAllBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBar.setAlignment(Pos.CENTER_RIGHT);

        aidTable = buildAidTable();
        VBox.setVgrow(aidTable, Priority.ALWAYS);

        tableCard.getChildren().addAll(tableTitle, searchBar, aidTable);

        page.getChildren().addAll(topRow, tableCard);
        refreshAidTable();
        applyRolePermissionsToAidPage();
        return page;
    }

    private TableView<AidDistribution> buildAidTable() {
        TableView<AidDistribution> table = new TableView<>();
        table.setItems(aidData);
        table.setPlaceholder(new Label("جدول التوزيع فارغ، أدخل بيانات أعلاه للحفظ."));

        table.getColumns().addAll(
                makeCol("رقم الإيصال", d -> d.getDistribution_ID()),
                makeCol("رقم العائلة", d -> String.valueOf(d.getReceiving_Family().getFamily_ID())),
                makeCol("اسم العائلة", d -> d.getReceiving_Family().getFamily_Name()),
                makeCol("الأفراد", d -> String.valueOf(d.getReceiving_Family().getMember_Count())),
                makeCol("نازحة؟", d -> d.getReceiving_Family().isIs_Displaced() ? "نعم" : "لا"),
                makeCol("الكمية", d -> String.valueOf(d.getDistributed_Quantity())),
                makeCol("الحالة", d -> d.getReceipt_status()),
                makeCol("تاريخ آخر مساعدة", d -> d.getReceiving_Family().getLast_Aid_Date())
        );

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) populateAidForm(newV);
        });
        return table;
    }

    private void populateAidForm(AidDistribution d) {
        editingDistId = d.getDistribution_ID();
        fldDistId.setText(d.getDistribution_ID());
        fldDistId.setDisable(true);
        Family f = d.getReceiving_Family();
        fldFamilyName.setText(f.getFamily_Name());
        fldMembers.setText(String.valueOf(f.getMember_Count()));
        fldContact.setText(f.getContact_Number());
        fldLastAidDate.setText(f.getLast_Aid_Date());
        fldQty.setText(String.valueOf(d.getDistributed_Quantity()));
        chkDisplaced.setSelected(f.isIs_Displaced());
        fldFamilyIdPreview.setText("رقم العائلة: " + f.getFamily_ID() + " (تعديل السجل)");
    }

    private void clearAidForm() {
        editingDistId = null;
        fldDistId.clear(); fldDistId.setDisable(false);
        fldFamilyName.clear(); fldMembers.clear(); fldContact.clear();
        fldLastAidDate.setText("لم يتلقَ"); fldQty.clear();
        chkDisplaced.setSelected(false);
        fldFamilyIdPreview.setText("سيتم توليد رقم عائلي فريد تلقائياً عند تأكيد الحفظ");
        aidTable.getSelectionModel().clearSelection();
    }

    private AidDistribution findById(String id) {
        for (AidDistribution d : distributionList) {
            if (d.getDistribution_ID().equalsIgnoreCase(id)) return d;
        }
        return null;
    }

    private int generateNextFamilyId() {
        int max = 100;
        for (AidDistribution d : distributionList) {
            if (d.getReceiving_Family().getFamily_ID() > max) max = d.getReceiving_Family().getFamily_ID();
        }
        return max + 1;
    }

    // =========================================================================
    // تم تعديل الدالة لضمان توافق مشيد الـ AidDistribution والحفظ الفوري واللحظي
    // =========================================================================
    private void handleRecordDistribution() {
        User activeUser = accountForRole(currentRole);
        boolean isOrg = activeUser.getUser_Type().equals("Organization");
        
        try {
            String distId = fldDistId.getText().trim();
            String famName = fldFamilyName.getText().trim();
            String membersStr = fldMembers.getText().trim();
            String phone = fldContact.getText().trim();
            String lastAid = fldLastAidDate.getText().trim();
            boolean displaced = chkDisplaced.isSelected();
            String qtyStr = fldQty.getText().trim();

            if (distId.isEmpty() || famName.isEmpty() || phone.isEmpty() || membersStr.isEmpty() || qtyStr.isEmpty()) {
                showModernAlert(Alert.AlertType.ERROR, "بيانات غير مكتملة", "الرجاء تعبئة جميع الخانات الرئيسية لحفظ العملية.");
                return;
            }

            int members = Integer.parseInt(membersStr);
            int qty = Integer.parseInt(qtyStr);

            if (members <= 0 || qty <= 0) {
                showModernAlert(Alert.AlertType.ERROR, "خطأ في القيّم الرقمية", "يجب أن تكون الأعداد المدخلة أكبر من الصفر.");
                return;
            }

            if (editingDistId == null) {
                // إضافة سجل جديد تماماً
                if (findById(distId) != null) {
                    showModernAlert(Alert.AlertType.ERROR, "تكرار المعرّف", "رقم إيصال التوزيع هذا مسجل مسبقاً بالنظام.");
                    return;
                }

                int newFamId = generateNextFamilyId();
                Family f = new Family(newFamId, famName, members, phone, displaced, lastAid);
                
                // ✔️ تم التعديل هنا: تمرير كائن المنظمة (unicef) لتلبية شروط المشيّد بشكل صحيح ومطابق للكلاس
                AidDistribution dist = new AidDistribution(distId, unicef, f, qty, "تم التسليم بنجاح");

                distributionList.add(dist);
                showModernAlert(Alert.AlertType.INFORMATION, "نجاح العملية", "تم تسجيل وحفظ إيصال التوزيع بنجاح في النظام والمزامنة مع الملف.");
            } else {
                // تعديل سجل قائم (هذه صلاحية حصرية للمؤسسة فقط!)
                if (!isOrg) {
                    showModernAlert(Alert.AlertType.ERROR, "خطأ في الصلاحيات", "عذراً! لا يمتلك المتطوع الميداني صلاحيات لتعديل السجلات المخزنة.");
                    return;
                }

                AidDistribution dist = findById(editingDistId);
                if (dist != null) {
                    Family f = dist.getReceiving_Family();
                    f.setFamily_Name(famName);
                    f.setMember_Count(members);
                    f.setContact_Number(phone);
                    f.setIs_Displaced(displaced);
                    f.setLast_Aid_Date(lastAid);
                    dist.setDistributed_Quantity(qty);
                    showModernAlert(Alert.AlertType.INFORMATION, "تم التحديث", "تم تحديث السجل المختار وحفظ التعديلات بنجاح.");
                }
            }

            // السطر الأهم: الحفظ الفوري واللحظي لقاعدة البيانات وتحديث الجدول
            DatabaseManager.saveToFile(distributionList); 
            clearAidForm();
            refreshAidTable();

        } catch (NumberFormatException e) {
            showModernAlert(Alert.AlertType.ERROR, "تنسيق غير صالح", "يرجى كتابة أرقام صحيحة في خانات (عدد الأفراد) و (الكمية).");
        }
    }

    private void handleDeleteDistribution() {
        boolean isOrg = accountForRole(currentRole).getUser_Type().equals("Organization");
        if (!isOrg) {
            showModernAlert(Alert.AlertType.ERROR, "فشل الإجراء", "صلاحية حذف السجلات معطلة بالكامل لحساب المتطوع.");
            return;
        }

        AidDistribution selected = aidTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showModernAlert(Alert.AlertType.WARNING, "تحديد مفقود", "يرجى تحديد السجل المراد حذفه من الجدول أدناه أولاً.");
            return;
        }

        distributionList.remove(selected);
        DatabaseManager.saveToFile(distributionList);
        showModernAlert(Alert.AlertType.INFORMATION, "تم الحذف", "تم إقصاء السجل وتحديث ملف قاعدة البيانات بنجاح.");
        clearAidForm();
        refreshAidTable();
    }

    private void applyRolePermissionsToAidPage() {
        boolean isOrg = accountForRole(currentRole).getUser_Type().equals("Organization");
        if (btnRecord == null) return; 

        if (isOrg) {
            btnRecord.setText("💾 تسجيل وحفظ (صلاحية كاملة)");
            btnDelete.setDisable(false);
            fldDistId.setDisable(false);
        } else {
            btnRecord.setText("💾 تسجيل وإرسال (إدخال جديد فقط)");
            btnDelete.setDisable(true); // قفل الحذف للمتطوع تماماً
        }
    }

    private void refreshAidTable() {
        aidData.setAll(distributionList);
    }

    private void doSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            refreshAidTable();
            return;
        }
        ArrayList<AidDistribution> filtered = new ArrayList<>();
        for (AidDistribution d : distributionList) {
            if (d.getReceiving_Family().getFamily_Name().toLowerCase().contains(query)) {
                filtered.add(d);
            }
        }
        aidData.setAll(filtered);
    }

    // =========================================================================
    // بقية الصفحات واللوحات الفرعية للنظام
    // =========================================================================
    private VBox buildOrganizationsPage() {
        VBox page = pageWrapper("الملف التعريفي للمنظمة", "البيانات المسجلة للمنظمة الشريكة المشرفة على هذا الوعاء التوزيعي.");

        VBox card = card();
        card.setMaxWidth(650);

        Label title = new Label("🏢 تفاصيل ترخيص الجهة المنفذة");
        title.setStyle(fontStyle + "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill:" + purple + ";");

        Label idLbl = new Label("رقم تعريف المنظمة (#" + unicef.getUser_ID() + ")");
        idLbl.setStyle(fontStyle + "-fx-font-size: 12px; -fx-text-fill:" + textMuted + ";");

        TextField fName = createField("اسم المنظمة");
        fName.setText(unicef.getUser_Name());

        TextField fType = createField("نوع الحساب");
        fType.setText(unicef.getUser_Type().equals("Organization") ? "منظمة شريكة (Organization)" : unicef.getUser_Type());

        TextField fArea = createField("مقر التغطية الإقليمي");
        fArea.setText(unicef.getCoverage_Area());

        fName.setDisable(true);
        fType.setDisable(true);

        Button btnSaveArea = new Button("💾 حفظ نطاق التغطية");
        styleButton(btnSaveArea, purple);
        btnSaveArea.setOnAction(e -> {
            String val = fArea.getText().trim();
            if (val.isEmpty()) {
                showModernAlert(Alert.AlertType.ERROR, "خطأ", "نطاق التغطية الإقليمي لا يمكن أن يكون فارغاً.");
                return;
            }
            unicef.setCoverage_Area(val);
            showModernAlert(Alert.AlertType.INFORMATION, "تم الحفظ", "تم تحديث نطاق التغطية الإقليمي بنجاح.");
        });

        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.15;");

        Label permTitle = new Label("🔑 صلاحيات الحساب الممنوحة");
        permTitle.setStyle(fontStyle + "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill:" + textDark + ";");

        Label permText = new Label(unicef.getRolePermissions());
        permText.setWrapText(true);
        permText.setStyle(fontStyle + "-fx-font-size: 12px; -fx-text-fill:" + textMuted + "; -fx-padding: 8; -fx-background-color:" + uiBg + "; -fx-background-radius: 8;");

        card.getChildren().addAll(title, idLbl, fName, fType, fArea, btnSaveArea, sep, permTitle, permText);
        page.getChildren().add(card);
        return page;
    }

    private VBox buildFamiliesPage() {
        VBox page = pageWrapper("قاعدة بيانات العائلات", "سجلات العائلات التي تلقت مساعدات موثقة لتجنب الازدواجية.");
        VBox card = card();
        VBox.setVgrow(card, Priority.ALWAYS);

        TableView<Family> famTable = new TableView<>();
        famTable.setItems(familiesData);
        VBox.setVgrow(famTable, Priority.ALWAYS);

        famTable.getColumns().addAll(
                makeFamCol("رقم الأسرة", f -> String.valueOf(f.getFamily_ID())),
                makeFamCol("اسم رب الأسرة", f -> f.getFamily_Name()),
                makeFamCol("الأفراد المستفيدين", f -> String.valueOf(f.getMember_Count())),
                makeFamCol("رقم الاتصال الموثق", f -> f.getContact_Number()),
                makeFamCol("النزوح المستجد", f -> f.isIs_Displaced() ? "نازح" : "مستقر"),
                makeFamCol("آخر مساعدة مستلمة", f -> f.getLast_Aid_Date())
        );

        card.getChildren().add(famTable);
        page.getChildren().add(card);
        return page;
    }

    private void refreshFamiliesData() {
        ArrayList<Family> list = new ArrayList<>();
        Set<Integer> addedIds = new HashSet<>();
        for (AidDistribution d : distributionList) {
            Family f = d.getReceiving_Family();
            if (!addedIds.contains(f.getFamily_ID())) {
                list.add(f);
                addedIds.add(f.getFamily_ID());
            }
        }
        familiesData.setAll(list);
    }

    private VBox buildProfilePage() {
        VBox page = pageWrapper("تحديث الملف الشخصي", "مراجعة معلومات الحساب المحلي وتحديث الهوية.");
        VBox card = card();
        card.setMaxWidth(600);

        Label title = new Label("🪪 تعديل هوية الحساب المفتوح حالياً");
        title.setStyle(fontStyle + "-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:" + purple + ";");

        User u = accountForRole(currentRole);

        TextField fldName = createField("اسم المستخدم للدخول");
        fldName.setText(u.getUser_Name());

        Button btnSave = new Button("حفظ وتحديث ملف البيانات");
        styleButton(btnSave, purple);
        btnSave.setOnAction(e -> {
            String val = fldName.getText().trim();
            if (val.isEmpty()) {
                showModernAlert(Alert.AlertType.ERROR, "خطأ", "اسم الحساب لا يمكن تركه فارغاً.");
                return;
            }
            u.setUser_Name(val);
            currentRole = val;
            updateSidebarProfile();
            showModernAlert(Alert.AlertType.INFORMATION, "تم الحفظ", "تم تحديث هويتك بنجاح.");
        });

        card.getChildren().addAll(title, fldName, btnSave);
        page.getChildren().add(card);
        return page;
    }

    // =========================================================================
    // أدوات تنسيق الواجهة المساعدة
    // =========================================================================
    private VBox pageWrapper(String titleText, String descText) {
        Label title = new Label(titleText);
        title.setStyle(fontStyle + "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill:" + textDark + ";");

        Label desc = new Label(descText);
        desc.setStyle(fontStyle + "-fx-font-size: 13px; -fx-text-fill:" + textMuted + ";");

        VBox header = new VBox(4, title, desc);
        header.setPadding(new Insets(0, 0, 10, 0));

        VBox box = new VBox(15, header);
        box.setAlignment(Pos.TOP_RIGHT);
        return box;
    }

    private VBox card() {
        VBox c = new VBox(12);
        c.setStyle("-fx-background-color:" + uiCardBg + "; -fx-border-color:" + uiBorder + "; -fx-border-radius:12; -fx-background-radius:12; -fx-padding:15;");
        c.setAlignment(Pos.TOP_RIGHT);
        return c;
    }

    private TextField createField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle(fontStyle + "-fx-padding: 8; -fx-background-radius: 6; -fx-border-color: " + uiBorder + "; -fx-border-radius: 6;");
        return f;
    }

    private void styleButton(Button b, String hexColor) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(fontStyle + "-fx-background-color: " + hexColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 16; -fx-background-radius: 6; -fx-cursor: hand;");
    }

    private <T> TableColumn<T, String> makeCol(String header, Function<T, String> func) {
        TableColumn<T, String> col = new TableColumn<>(header);
        col.setCellValueFactory(data -> new SimpleStringProperty(func.apply(data.getValue())));
        col.setPrefWidth(120);
        return col;
    }

    private <T> TableColumn<T, String> makeFamCol(String header, Function<T, String> func) {
        TableColumn<T, String> col = new TableColumn<>(header);
        col.setCellValueFactory(data -> new SimpleStringProperty(func.apply(data.getValue())));
        col.setPrefWidth(150);
        return col;
    }

    private void showModernAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.getDialogPane().getScene().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        alert.showAndWait();
    }
}