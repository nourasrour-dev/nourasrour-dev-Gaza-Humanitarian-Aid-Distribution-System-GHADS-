package gaza.aid.tracker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GHADSApp extends Application {

    private static final int INITIAL_STOCK = 5000;
    private ArrayList<AidDistribution> distributionList = new ArrayList<>();
    private Organization unicef = new Organization(1, "UNICEF_Gaza", "123", "Organization", "Central Area");
    private Volunteer fieldVolunteer = new Volunteer(2, "Ahmad_Volunteer", "123", "Central Area");

    // ألوان وتنسيقات الواجهة الموحدة
    private final String uiBg = "#f1f5f9", uiCardBg = "#ffffff", uiBorder = "#cbd5e1";
    private final String brandGreen = "#0f766e", brandBlue = "#0284c7", textMuted = "#64748b";
    private final String fontStyle = "-fx-font-family: 'Segoe UI', 'Arial'; ";

    // عناصر التحكم الرئيسية
    private Label statLabel1 = new Label("0"), statLabel2 = new Label("5000"), statLabel3 = new Label("0"), statLabel4 = new Label("5000");
    private TextArea displayArea = new TextArea();
    private Label statusLabel = new Label(" حالة النظام: نشط وفي انتظار العمليات.");
    private ComboBox<String> roleComboBox = new ComboBox<>();
    private String currentRole = "متطوع";

    private Button btnOpenAddPopup = new Button("تسجيل توزيع جديد ➕");
    private Button btnSortUrgency = new Button("تطبيق فرز الأولوية الخوارزمي ⚠️");
    private Button btnEditRecord = new Button("تعديل السجل ✏️");
    private Button btnDeleteRecord = new Button("حذف السجل 🗑️");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GHADS Pro - لوحة تتبع المساعدات والموارد لقطاع غزة");
        distributionList = DatabaseManager.loadFromFile(unicef);

        // 1. الترويسة الرئيسية
        VBox appHeader = new VBox(6);
        appHeader.setStyle("-fx-background-color: linear-gradient(to right, #0d9488, " + brandGreen + "); -fx-padding: 22 25;");
        appHeader.setAlignment(Pos.TOP_RIGHT);
        
        Label titleLbl = new Label("نظام GHADS Pro • النظام الموحد لتوزيع المساعدات الإنسانية في غزة");
        titleLbl.setStyle(fontStyle + "-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: white;");
        Label subTitleLbl = new Label("لوحة مركزية متكاملة لتخصيص الموارد، صلاحيات الأدوار، وفرز الحالات في الوقت الفعلي.");
        subTitleLbl.setStyle(fontStyle + "-fx-font-size: 12px; -fx-text-fill: #ccfbf1;");
        appHeader.getChildren().addAll(titleLbl, subTitleLbl);

        // 2. شبكة كروت البيانات التفاعلية (Dashboard Cards)
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setPadding(new Insets(20, 25, 15, 25));
        statsGrid.setStyle("-fx-background-color: " + uiBg + ";");
        statsGrid.add(buildStatCard("إجمالي العائلات المسجلة📋", statLabel1, brandBlue, "#f0f9ff"), 3, 0);
        statsGrid.add(buildStatCard("المخزون الافتتاحي للمستودع📰", statLabel2, "#0d9488", "#f0fdfa"), 2, 0);
        statsGrid.add(buildStatCard("إجمالي الوحدات الموزعة📊", statLabel3, "#b45309", "#fffbeb"), 1, 0);
        statsGrid.add(buildStatCard("صافي الاحتياطي الآمن💻", statLabel4, "#e11d48", "#fff1f2"), 0, 0);

        ColumnConstraints colConst = new ColumnConstraints();
        colConst.setPercentWidth(25);
        statsGrid.getColumnConstraints().addAll(colConst, colConst, colConst, colConst);

        // 3. اللوحة الجانبية (Sidebar)
        VBox actionsSidebar = new VBox(14);
        actionsSidebar.setPadding(new Insets(15));
        actionsSidebar.setStyle("-fx-background-color: " + uiCardBg + "; -fx-border-color: " + uiBorder + "; -fx-border-radius: 12; -fx-background-radius: 12;");
        actionsSidebar.setPrefWidth(290);
        actionsSidebar.setAlignment(Pos.TOP_RIGHT);

        Label authTitle = new Label("🔐 وحدة التحكم بالصلاحيات");
        authTitle.setStyle(fontStyle + "-fx-font-weight: bold; -fx-text-fill: " + brandGreen + "; -fx-font-size: 13px;");

        roleComboBox.getItems().addAll("متطوع", "منظمة (Organization)");
        roleComboBox.setValue("متطوع");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);
        roleComboBox.setStyle("-fx-node-orientation: right-to-left;");

        Label permissionsLabel = new Label(fieldVolunteer.getRolePermissions());
        permissionsLabel.setWrapText(true);
        permissionsLabel.setStyle("-fx-text-fill: #1565c0; -fx-font-weight: bold; -fx-background-color: #e3f2fd; -fx-padding: 8; -fx-font-size: 11px; -fx-node-orientation: right-to-left; -fx-border-radius: 4;");

        roleComboBox.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            if (selectedRole == null || selectedRole.equals(currentRole)) return;

            if (promptPasswordDialog(selectedRole, primaryStage)) {
                currentRole = selectedRole;
                updateRolePermissions(permissionsLabel);
            } else {
                Platform.runLater(() -> roleComboBox.setValue(currentRole));
            }
        });

        VBox authBox = new VBox(8, authTitle, roleComboBox, permissionsLabel);
        authBox.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
        authBox.setAlignment(Pos.TOP_RIGHT);

        styleButton(btnOpenAddPopup, "#0d9488");
        btnOpenAddPopup.setOnAction(e -> showAddRecordPopup(primaryStage));

        styleButton(btnEditRecord, brandBlue);
        btnEditRecord.setDisable(true);
        btnEditRecord.setOnAction(e -> showEditRecordPopup(primaryStage));

        styleButton(btnDeleteRecord, "#9f1239");
        btnDeleteRecord.setDisable(true);
        btnDeleteRecord.setOnAction(e -> showDeleteRecordPopup(primaryStage));

        styleButton(btnSortUrgency, "#e11d48");
        btnSortUrgency.setOnAction(e -> {
            if (distributionList.isEmpty()) {
                statusLabel.setText(" ⚠  خطأ: لا توجد بيانات لتطبيق الفرز.");
                return;
            }
            java.util.Collections.sort(distributionList);
            refreshDisplay();
            statusLabel.setText(" ⚠ 💻 تم تحديث الأولويات بحسب شدة الاحتياج والنزوح.");
        });

        actionsSidebar.getChildren().addAll(authBox, new Separator(), new Label("منصة العمليات:"),
                btnOpenAddPopup, btnEditRecord, btnDeleteRecord, btnSortUrgency);

        // 4. صندوق عرض السجلات
        VBox logWrapper = new VBox(10);
        logWrapper.setPadding(new Insets(15));
        logWrapper.setStyle("-fx-background-color: " + uiCardBg + "; -fx-border-color: " + uiBorder + "; -fx-border-radius: 12; -fx-background-radius: 12;");
        HBox.setHgrow(logWrapper, Priority.ALWAYS);

        HBox logHeader = new HBox(5, new Label("شاشة مراقبة سجل المساعدات الموحد والآمن 💻"), new Region(),
                new javafx.scene.shape.Circle(4, Color.GREEN), new javafx.scene.shape.Circle(4, Color.ORANGE), new javafx.scene.shape.Circle(4, Color.RED));
        logHeader.setAlignment(Pos.CENTER_RIGHT);

        displayArea.setEditable(false);
        displayArea.setStyle(fontStyle + "-fx-font-size: 13px; -fx-text-fill: #38bdf8; -fx-control-inner-background: #0f172a; -fx-node-orientation: right-to-left;");
        VBox.setVgrow(displayArea, Priority.ALWAYS);
        
        statusLabel.setStyle(fontStyle + "-fx-font-size: 12px;");
        logWrapper.getChildren().addAll(logHeader, displayArea, statusLabel);

        HBox coreLayout = new HBox(20, logWrapper, actionsSidebar);
        coreLayout.setPadding(new Insets(10, 25, 20, 25));
        VBox.setVgrow(coreLayout, Priority.ALWAYS);

        VBox mainLayout = new VBox(appHeader, statsGrid, coreLayout);
        mainLayout.setStyle("-fx-background-color: " + uiBg + ";");
        primaryStage.setScene(new Scene(mainLayout, 1020, 680));
        refreshDisplay();
        primaryStage.show();
    }

    // دالة مساعدة لإنشاء وتنسيق الحقول النصية لتقليص الكود ومنع التكرار
    private TextField createField(String promptText) {
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setStyle("-fx-node-orientation: right-to-left; -fx-padding: 8; -fx-background-radius: 6;");
        return tf;
    }

    private boolean promptPasswordDialog(String role, Stage owner) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("بوابة التحقق");
        dialog.setHeaderText("دخول بصلاحية: " + role);
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getDialogPane().setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);

        ButtonType loginBtnType = new ButtonType("تسجيل الدخول", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginBtnType, ButtonType.CANCEL);

        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("كلمة المرور");
        pwdField.setStyle("-fx-background-radius: 6; -fx-padding: 8;");

        VBox content = new VBox(10, new Label("أدخل كلمة المرور الخاصة بحساب " + role + ":"), pwdField);
        content.setPadding(new Insets(15));
        content.setPrefWidth(320);
        dialog.getDialogPane().setContent(content);

        Platform.runLater(pwdField::requestFocus);
        dialog.setResultConverter(btn -> btn == loginBtnType ? pwdField.getText() : null);

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (accountForRole(role).checkPassword(result.get())) {
                showModernAlert(Alert.AlertType.INFORMATION, "تم التحقق", "أهلاً بك! تم تسجيل الدخول بنجاح.");
                return true;
            }
            showModernAlert(Alert.AlertType.ERROR, "فشل التحقق", "كلمة المرور المدخلة غير صحيحة!");
        }
        return false;
    }

    private void updateRolePermissions(Label permissionsLabel) {
        boolean isOrg = currentRole.startsWith("منظمة");
        btnOpenAddPopup.setDisable(false);
        btnEditRecord.setDisable(!isOrg);
        btnDeleteRecord.setDisable(!isOrg);

        permissionsLabel.setText(accountForRole(currentRole).getRolePermissions());
        if (isOrg) {
            permissionsLabel.setStyle("-fx-text-fill: #0f766e; -fx-font-weight: bold; -fx-background-color: #ccfbf1; -fx-padding: 8; -fx-font-size: 11px; -fx-border-radius: 4;");
            statusLabel.setText(" حالة النظام: صلاحيات التعديل والحذف الكاملة للمنظمة نشطة الآن.");
        } else {
            permissionsLabel.setStyle("-fx-text-fill: #1565c0; -fx-font-weight: bold; -fx-background-color: #e3f2fd; -fx-padding: 8; -fx-font-size: 11px; -fx-border-radius: 4;");
            statusLabel.setText(" حالة النظام: دور المتطوع مسموح له بالعرض والإضافة فقط.");
        }
    }

    private void showAddRecordPopup(Stage ownerStage) {
        Stage popupStage = new Stage();
        popupStage.setTitle("استمارة تسجيل حركة توزيع جديدة");
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(ownerStage);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff;");
        layout.setAlignment(Pos.TOP_RIGHT);

        Label formTitle = new Label("أدخل تفاصيل سجل التوزيع");
        formTitle.setStyle(fontStyle + "-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + brandGreen + ";");

        TextField idField = createField("رقم إيصال التوزيع (مثال: REC-01)");
        TextField nameField = createField("اسم رب الأسرة الكامل");
        TextField countField = createField("عدد أفراد الأسرة الإجمالي");
        TextField phoneField = createField("رقم هاتف التواصل");
        TextField dateField = createField("تاريخ آخر مساعدة (سنة-شهر-يوم أو لم يتلقَ)");
        dateField.setText("لم يتلقَ");
        TextField qtyField = createField("الكمية الموزعة (بالوحدات)");

        CheckBox dispCheck = new CheckBox("هل العائلة نازحة؟");
        dispCheck.setStyle(fontStyle + "-fx-text-fill: #1e293b; -fx-node-orientation: right-to-left;");

        Button saveBtn = new Button("اعتماد وحفظ في السجل المركزي");
        styleButton(saveBtn, brandGreen);

        saveBtn.setOnAction(e -> {
            try {
                String distId = idField.getText().trim();
                String famName = nameField.getText().trim();
                int members = Integer.parseInt(countField.getText().trim());
                String phone = phoneField.getText().trim();
                String lastAid = Family.normalizeLastAidDate(dateField.getText());
                boolean displaced = dispCheck.isSelected();
                int qty = Integer.parseInt(qtyField.getText().trim());

                if (distId.isEmpty() || famName.isEmpty() || phone.isEmpty() || members <= 0 || qty <= 0) throw new IllegalArgumentException();
                if (findById(distId) != null) {
                    showModernAlert(Alert.AlertType.ERROR, "معرّف مكرر", "رقم الإيصال هذا موجود بالفعل.");
                    return;
                }

                Family f = new Family(generateNextFamilyId(), famName, members, phone, displaced, lastAid);
                if (f.isEligibleForAid() && qty > getAvailableStock()) {
                    showModernAlert(Alert.AlertType.ERROR, "الكمية غير متاحة", "الكمية المطلوبة أكبر من المخزون المتبقي (" + getAvailableStock() + " وحدة).");
                    return;
                }

                AidDistribution dist = new AidDistribution(distId, unicef, f, qty);
                distributionList.add(dist);
                DatabaseManager.saveToFile(distributionList);
                refreshDisplay();
                statusLabel.setText(" ✅ تم حفظ السجل رقم " + distId + " بالحالة: " + dist.getReceipt_status());
                popupStage.close();
            } catch (Exception ex) {
                showModernAlert(Alert.AlertType.ERROR, "فشل التحقق", "تأكد من صحة المدخلات وعدم وجود حقول فارغة.");
            }
        });

        layout.getChildren().addAll(formTitle, idField, nameField, countField, phoneField, dateField, qtyField, dispCheck, saveBtn);
        popupStage.setScene(new Scene(layout, 360, 480));
        popupStage.showAndWait();
    }

    private void showDeleteRecordPopup(Stage ownerStage) {
        if (distributionList.isEmpty()) {
            showModernAlert(Alert.AlertType.INFORMATION, "السجل فارغ", "لا توجد أي سجلات متاحة للحذف.");
            return;
        }

        Stage popupStage = new Stage();
        popupStage.setTitle("حذف سجل توزيع");
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(ownerStage);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_RIGHT);
        layout.setStyle("-fx-background-color: #ffffff;");

        ComboBox<String> idBox = new ComboBox<>();
        for (AidDistribution d : distributionList) idBox.getItems().add(d.getDistribution_ID());
        idBox.setMaxWidth(Double.MAX_VALUE);
        idBox.setStyle("-fx-node-orientation: right-to-left;");

        Button confirmBtn = new Button("تأكيد الحذف النهائي");
        styleButton(confirmBtn, "#9f1239");

        confirmBtn.setOnAction(e -> {
            String selectedId = idBox.getValue();
            if (selectedId == null) return;
            AidDistribution target = findById(selectedId);
            if (target != null) {
                distributionList.remove(target);
                DatabaseManager.saveToFile(distributionList);
                refreshDisplay();
                statusLabel.setText(" 🗑️ تم حذف السجل " + selectedId + " بنجاح.");
            }
            popupStage.close();
        });

        layout.getChildren().addAll(new Label("اختر رقم الإيصال للمسح:"), idBox, confirmBtn);
        popupStage.setScene(new Scene(layout, 320, 180));
        popupStage.showAndWait();
    }

    private void showEditRecordPopup(Stage ownerStage) {
        if (distributionList.isEmpty()) {
            showModernAlert(Alert.AlertType.INFORMATION, "السجل فارغ", "لا توجد سجلات للتعديل.");
            return;
        }

        Stage popupStage = new Stage();
        popupStage.setTitle("تعديل بيانات سجل التوزيع");
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(ownerStage);

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_RIGHT);
        layout.setStyle("-fx-background-color: #ffffff;");

        ComboBox<String> idBox = new ComboBox<>();
        for (AidDistribution d : distributionList) idBox.getItems().add(d.getDistribution_ID());
        idBox.setMaxWidth(Double.MAX_VALUE);
        idBox.setStyle("-fx-node-orientation: right-to-left;");

        TextField countField = createField("تعديل عدد أفراد الأسرة");
        TextField phoneField = createField("تعديل رقم هاتف التواصل");
        TextField qtyField = createField("تعديل الكمية الموزعة");
        CheckBox dispCheck = new CheckBox("العائلة نازحة");
        dispCheck.setStyle("-fx-node-orientation: right-to-left;");

        VBox editFields = new VBox(8, countField, phoneField, qtyField, dispCheck);
        editFields.setDisable(true);
        editFields.setAlignment(Pos.TOP_RIGHT);

        idBox.setOnAction(e -> {
            AidDistribution target = findById(idBox.getValue());
            if (target != null) {
                Family f = target.getReceiving_Family();
                countField.setText(String.valueOf(f.getMember_Count()));
                phoneField.setText(f.getContact_Number());
                qtyField.setText(String.valueOf(target.getDistributed_Quantity()));
                dispCheck.setSelected(f.isIs_Displaced());
                editFields.setDisable(false);
            }
        });

        Button saveBtn = new Button("حفظ التغييرات الجديدة");
        styleButton(saveBtn, brandGreen);

        saveBtn.setOnAction(e -> {
            AidDistribution target = findById(idBox.getValue());
            if (target == null) return;
            try {
                int members = Integer.parseInt(countField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());
                String phone = phoneField.getText().trim();

                if (members <= 0 || qty <= 0 || phone.isEmpty()) throw new IllegalArgumentException();

                if (target.isDelivered()) {
                    int availableForTarget = INITIAL_STOCK - (getTotalDeliveredUnits() - target.getDistributed_Quantity());
                    if (qty > availableForTarget) {
                        showModernAlert(Alert.AlertType.ERROR, "الكمية غير متاحة", "الحد الأقصى المتاح هو " + availableForTarget + " وحدة.");
                        return;
                    }
                }

                Family f = target.getReceiving_Family();
                f.setMember_Count(members);
                f.setContact_Number(phone);
                f.setIs_Displaced(dispCheck.isSelected());
                target.setDistributed_Quantity(qty);

                DatabaseManager.saveToFile(distributionList);
                refreshDisplay();
                statusLabel.setText(" ✏ تم تحديث السجل بنجاح.");
                popupStage.close();
            } catch (Exception ex) {
                showModernAlert(Alert.AlertType.ERROR, "فشل التحقق", "تأكد من صحة القيم الرقمية.");
            }
        });

        layout.getChildren().addAll(new Label("اختر السجل للتعديل:"), idBox, editFields, saveBtn);
        popupStage.setScene(new Scene(layout, 340, 380));
        popupStage.showAndWait();
    }

    private User accountForRole(String role) {
        return (role != null && role.startsWith("منظمة")) ? unicef : fieldVolunteer;
    }

    private AidDistribution findById(String distributionId) {
        for (AidDistribution d : distributionList) {
            if (d.getDistribution_ID().equals(distributionId)) return d;
        }
        return null;
    }

    private void refreshDisplay() {
        Set<Integer> uniqueFamilyIds = new HashSet<>();
        for (AidDistribution dist : distributionList) uniqueFamilyIds.add(dist.getReceiving_Family().getFamily_ID());

        statLabel1.setText(String.valueOf(uniqueFamilyIds.size()));
        statLabel2.setText(String.valueOf(INITIAL_STOCK));

        int totalReleased = getTotalDeliveredUnits();
        statLabel3.setText(String.valueOf(totalReleased));
        statLabel4.setText(String.valueOf(INITIAL_STOCK - totalReleased));

        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================================\n")
                .append("               شاشة مراقبة السجل المركزي الموحد لتوزيع المساعدات                 \n")
                .append("=========================================================================\n\n");

        if (distributionList.isEmpty()) {
            sb.append(" [تنبيه النظام] لا توجد مدخلات ميدانية في قاعدة البيانات حتى الآن.\n");
        } else {
            for (AidDistribution dist : distributionList) {
                sb.append(dist.toString()).append("-------------------------------------------------------------------------\n");
            }
        }
        displayArea.setText(sb.toString());
    }

    private int getTotalDeliveredUnits() {
        int total = 0;
        for (AidDistribution dist : distributionList) {
            if (dist.isDelivered()) total += dist.getDistributed_Quantity();
        }
        return total;
    }

    private int getAvailableStock() {
        return INITIAL_STOCK - getTotalDeliveredUnits();
    }

    private int generateNextFamilyId() {
        int maxId = 99;
        for (AidDistribution dist : distributionList) {
            maxId = Math.max(maxId, dist.getReceiving_Family().getFamily_ID());
        }
        return maxId + 1;
    }

    private VBox buildStatCard(String title, Label val, String hex, String bgHex) {
        Label t = new Label(title);
        t.setStyle(fontStyle + "-fx-font-size:12px; -fx-text-fill:" + textMuted + "; -fx-font-weight:bold;");
        val.setStyle(fontStyle + "-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + hex + ";");
        VBox c = new VBox(5, t, val);
        c.setStyle("-fx-background-color:" + bgHex + "; -fx-border-color:" + uiBorder + "; -fx-border-radius:8; -fx-background-radius:8; -fx-padding:15;");
        c.setAlignment(Pos.TOP_RIGHT);
        return c;
    }

    private void styleButton(Button b, String hex) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(fontStyle + "-fx-background-color:" + hex + "; -fx-text-fill:white; -fx-font-weight:bold; -fx-padding:10; -fx-background-radius:6; -fx-cursor: hand;");
    }

    private void showModernAlert(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type, text, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        Application.launch(GHADSApp.class, args);
    }
}