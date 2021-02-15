import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import javassist.compiler.ast.Visitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static ArrayList<String> memory_classlibrary = new ArrayList<>();
    public static HashMap<String, ArrayList<ImportDeclaration>> memory_import = new HashMap<>();
    public static ArrayList<String> memory_classname = new ArrayList<>();
    public static HashMap<String, String> memory_extend = new HashMap<>();
    public static HashMap<String, ArrayList<String>> memory_implement = new HashMap<>();
    public static HashMap<String, List<FieldDeclaration>> memory_classfield = new HashMap<>();
    public static HashMap<String, List<MethodDeclaration>> memory_classmethod = new HashMap<>();
    public static HashMap<String, ArrayList<String>> memory_innerclass = new HashMap<>();
    public static HashMap<String, List<ConstructorDeclaration>> memory_constructor = new HashMap<>();

    public static void main(String[] args) throws IOException {

        String path_root ="/Users/kzm0308/Desktop/workspace/BabyProfessorAndroid-master/src/com/kilobolt";
        //last"/Users/kzm0308/Desktop/workspace/BabyProfessorAndroid-master/src/com/kilobolt";
        //multi"/Users/kzm0308/Desktop/workspace/android-vision-master/visionSamples/multi-tracker/app/src/main/java/com/google/android/gms/samples/vision/face/multitracker";
        //webRTC"/Users/kzm0308/Desktop/workspace/glass-enterprise-samples-master/WebRTCSample/app/src/org/appspot/apprtcstandalone";
        //facetracker"/Users/kzm0308/Desktop/workspace/android-vision-master/visionSamples/FaceTracker/app/src/main/java/com/google/android/gms/samples/vision/face/facetracker";
        //polygallery"/Users/kzm0308/Desktop/workspace/sceneform-poly-browser-master/app/src/main/java/com/google/devrel/ar/sample/polygallery";
        //card"/Users/kzm0308/Desktop/workspace/glass-enterprise-samples-master/CardSample/app/src/main/java/com/example/android/glass/cardsample";
        //googly-eyes"/Users/kzm0308/Desktop/workspace/android-vision-master/visionSamples/googly-eyes/app/src/main/java/com/google/android/gms/samples/vision/face/googlyeyes";
        //camera2"/Users/kzm0308/Desktop/workspace/glass-enterprise-samples-master/Camera2Sample/app/src/main/java/com/example/glass/camera2sample";
        //gallery"/Users/kzm0308/Desktop/workspace/glass-enterprise-samples-master/GallerySample/app/src/main/java/com/example/glass/gallerysample";
        //orc-comp"/Users/kzm0308/Desktop/workspace/android-vision-master/visionSamples/ocr-codelab/ocr-reader-complete/app/src/main/java/com/google/android/gms/samples/vision/ocrreader";
        //voiceRecog"/Users/kzm0308/Desktop/workspace/glass-enterprise-samples-master/VoiceRecognitionSample/app/src/main/java/com/example/glass/voicerecognitionsample";
        //guesslibrary"/Users/kzm0308/Desktop/workspace/glass-enterprise-samples-master/GestureLibrarySample/main/src/main/java/com/example/glass/ui";
        //QRcodeScanner"/Users/kzm0308/Desktop/workspace/glass-enterprise-samples-master/QRCodeScannerSample/app/src/main/java/com/example/glass/qrcodescannersample";
        //orc-record"/Users/kzm0308/Desktop/workspace/android-vision-master/visionSamples/ocr-reader/app/src/main/java/com/google/android/gms/samples/vision/ocrreader";
        //barcode"/Users/kzm0308/Desktop/workspace/android-vision-master/visionSamples/barcode-reader/app/src/main/java/com/google/android/gms/samples/vision/barcodereader";
        //flip-tester"/Users/kzm0308/Desktop/workspace/identity-appflip-tester-android-master/app/src/main/java/com/google/appfliptesttool";
        //flip-master"/Users/kzm0308/Desktop/workspace/identity-appflip-android-master/app/src/main/java/com/google/appflip_sample_android";
        //safenet"/Users/kzm0308/Desktop/workspace/android-play-safetynet-master/client/java/SafetyNetSample/Application/src/main/java/com/example/android";
        //PBG"/Users/kzm0308/Desktop/workspace/PartyBattleGame/app/src/main/java/com/example/kzm/partybattlegame";

        SourceRoot root = new SourceRoot(Paths.get(path_root));
        List<ParseResult<CompilationUnit>> cu2 = root.tryToParse("");

        String tmpname = "/Users/kzm0308/.gradle/caches/modules-2/files-2.1/com.github.javaparser/javaparser-core/3.16.1/440e5d7118e50d544418a96873d6322c4b1d5527";

        ProjectRoot projectRoot =
                new SymbolSolverCollectionStrategy()
                        .collect(Paths.get("/Users/kzm0308/.gradle/caches/modules-2/files-2.1/com.github.javaparser/javaparser-core/3.16.1/440e5d7118e50d544418a96873d6322c4b1d5527/javaparser-core-3.16.1.jar!/com/github/javaparser"));

        //情報収集フェーズ
        for(int i = 0; i < cu2.size(); i++){
            VoidVisitor<?> visitor = new FirstVisitor();
            cu2.get(i).getResult().get().accept(visitor, null);
        }
        //判断・警告フェーズ(出力)
        for(String classname:memory_classname){
            System.out.println("\ncheck start:"+classname+"\n");
            fullcheck_import(classname);
            check_initialize(classname);
            judge_case1(classname);
            judge_case2(classname);
            judge_case3(classname);
            judge_case5and7(classname, "case5");
            judge_case5and7(classname, "case7");
            System.out.println("check finished:"+classname+"\n");
        }

        for(String name:memory_classlibrary){
            System.out.println(name);
        }

    }

    public static void judge_case1(String key){
        System.out.println("check start:case1\n");
        if(memory_classfield.get(key) != null) {
            boolean already_warning_interface = false;
            boolean already_warning_superclass = false;
            for (FieldDeclaration field : memory_classfield.get(key)) {
                int size = field.getVariables().size();
                for (int i = 0; i < size; i++) {
                    String extend_field = field.getVariable(i).getNameAsString();
                    //implementsの確認、再帰関数使用

                    if (memory_implement.get(key) != null) {
                        for (String name_interface : memory_implement.get(key)) {
                            already_warning_interface = check_import(name_interface, already_warning_interface);
                            if (!check_import(name_interface, already_warning_interface)) check_ImplementField(name_interface, extend_field);
                        }
                    }

                    //extendsの確認、再帰関数使用
                    if (memory_extend.get(key) != null) {
                        already_warning_superclass = check_import(memory_extend.get(key), already_warning_superclass);
                        if (!check_import(memory_extend.get(key), already_warning_superclass)) check_ExtendField(memory_extend.get(key), extend_field);
                    }
                }
            }
        }
        System.out.println("check finished:case1\n");
    }

    public static void judge_case2(String key){
        System.out.println("check start:case2\n");
        if(memory_classmethod.get(key) != null) {
            for (MethodDeclaration detail : memory_classmethod.get(key)) {
                //get/set探す部分
                String methodname = detail.getNameAsString();
                String cut_field = "";

                if (search_get("case2", detail))
                    cut_field = methodname.split("get")[1].toLowerCase();
                if (!cut_field.equals("")) {
                    if (!match_field(key, cut_field)) {
                        System.out.print("line " + detail.getRange().get().begin.line);
                        System.out.println("-" + detail.getRange().get().end.line);
                        System.out.println("method \"" + methodname + "\" is existing getter" +
                                " but field \"" + cut_field + "\" is not existing.");
                        System.out.println("If a correction is necessary, you should change methodname from "
                                + methodname + " to " + methodname.toLowerCase() + " or other name.\n");
                    }
                }
                cut_field = "";
                if (search_set("case2", detail))
                    cut_field = methodname.split("set")[1].toLowerCase();
                if (!cut_field.equals("")) {
                    if (!match_field(key, cut_field)) {
                        System.out.print("line " + detail.getRange().get().begin.line);
                        System.out.println("-" + detail.getRange().get().end.line);
                        System.out.println("method \"" + methodname + "\" is existing setter" +
                                " but field \"" + cut_field + "\" is not existing.");
                        System.out.println("If a correction is necessary, you should change methodname from "
                                + methodname + " to " + methodname.toLowerCase() + " or other name.\n");
                    }
                }

            }
        }
        System.out.println("check finished:case2\n");
    }

    public static void judge_case3(String key){
        System.out.println("check start:case3\n");
        if(memory_innerclass.get(key) != null) {
            for (String inner : memory_innerclass.get(key)) {
                if (memory_classmethod.get(inner) == null) break;
                for (MethodDeclaration md : memory_classmethod.get(inner)) {
                    NodeList modifiers = md.getModifiers();
                    boolean flag = false;
                    int mod_size = modifiers.size();
                    if (mod_size == 0) continue;
                    String name = md.getNameAsString();
                    for (int i = 0; i < mod_size; i++) {
                        if (modifiers.get(i).toString().matches("private ")) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        for (MethodDeclaration enclosing : memory_classmethod.get(key)) {
                            VoidVisitor<?> visitor = new Checking_Case3(name);
                            enclosing.accept(visitor, null);//visitorを利用して捜索と警告を両方行う
                        }
                    }
                }
            }
        }
        System.out.println("check finished:case3\n");
    }

    public static void judge_case5and7(String key, String mode){
        System.out.println("check start:" + mode+"\n");
            List<MethodDeclaration> judge_array = memory_classmethod.get(key);//今のクラスのフィールド取得
            if(judge_array != null) {
                for (MethodDeclaration detail:judge_array) {//フィールドを１つずつ見る

                    int size = detail.getParameters().size();
                    if (size == 0) continue;
                    else {
                        String methodname = detail.getNameAsString();
                        for (int i = 0; i < size; i++) {

                            String param = detail.getParameter(i).getNameAsString();
                            VoidVisitor<?> visitor = new Checking_Case5and7(key, methodname, param, mode);
                            detail.accept(visitor, null);//visitorを利用して捜索と警告を両方行う

                        }
                    }
                }
            }
        System.out.println("check finished:"+ mode +"\n");
    }

    public static void check_ExtendField(String origin, String extend_field){
        if(memory_classfield.get(origin) != null){
        for(FieldDeclaration field:memory_classfield.get(origin)){
            int size = field.getVariables().size();
            for (int i = 0; i < size; i++) {
                String origin_field = field.getVariable(i).getNameAsString();
                if (extend_field.equals(origin_field)) {
                    System.out.println("same name field:" + origin_field + "(origin), "
                            + extend_field + "(extends)"
                            + "\nIf a correction is necessary, you should change name of field(extends).\n");
                    break;
                }
            }
        }}
        if(memory_extend.get(origin) != null) check_ExtendField(memory_extend.get(origin), extend_field);
        if(memory_implement.get(origin) != null){
            for(String key:memory_implement.get(origin)) check_ImplementField(key, extend_field);
        }
    }

    public static void check_ImplementField(String origin, String implement_field){
        if(memory_classfield.get(origin) != null) {
            for (FieldDeclaration field : memory_classfield.get(origin)) {
                int size = field.getVariables().size();
                for (int i = 0; i < size; i++) {
                    String origin_field = field.getVariable(i).getNameAsString();
                    if (implement_field.equals(origin_field)) {
                        System.out.println("same name field:" + origin_field + "(interface), "
                                + implement_field + "(implements-class)"
                                + "\nIf a correction is necessary, you should change name of field(implements-class).\n");
                        break;
                    }
                }
            }
        }
        if(memory_extend.get(origin) != null) check_ExtendField(origin, implement_field);
    }

    public static boolean search_get(String mode, MethodDeclaration detail){
        String methodname = detail.getNameAsString();
        boolean flag = false;

        if (methodname.matches("get[A-Z].*")) {
            if (detail.getParameters().isEmpty()) {
                String returns = detail.getTypeAsString();
                if (!returns.equals("void") || (returns.equals("void") && !mode.equals("case2"))) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static boolean search_set(String mode, MethodDeclaration detail){
        String methodname = detail.getNameAsString();
        boolean flag = false;
        if (methodname.matches("set[A-Z].*")) {
            int size_param = detail.getParameters().size();
            if (size_param == 1) {
                String returns = detail.getTypeAsString();
                if (returns.equals("void")) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static boolean match_field(String method, String xxx){
        if(memory_classfield.get(method) != null){
            for (FieldDeclaration field : memory_classfield.get(method)) {
                int size = field.getVariables().size();
                for (int i = 0; i < size; i++) {
                    String fieldname = field.getVariable(i).getNameAsString();
                    if (fieldname.equals(xxx) || fieldname.toLowerCase().equals(xxx) || fieldname.toUpperCase().equals(xxx)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean check_ExtendMethod(String origin, String mode){
        List<MethodDeclaration> check_array = memory_classmethod.get(origin);
        boolean found = false;
        if(check_array != null) {
            for(MethodDeclaration detail:check_array) {
                found = search_get(mode, detail);
                if (!found) {
                    found = search_set(mode, detail);
                    if (!found) {
                        continue;
                    } else break;
                } else break;
            }
        }
        if(memory_extend.get(origin) != null && !found){
            found = check_ExtendMethod(memory_extend.get(origin), mode);
        }
        if(memory_implement.get(origin) != null && !found){
            for(String key:memory_implement.get(origin)){
                found = check_ImplementMethod(key, mode);
                if(found) break;
            }
        }
        //found = check_import(origin, false);
        return found;
    }

    public static boolean check_ImplementMethod(String origin, String mode){
        List<MethodDeclaration> check_array = memory_classmethod.get(origin);
        boolean found = false;
        if(check_array != null) {
            for(MethodDeclaration detail:check_array) {
                found = search_get(mode, detail);
                if (!found) {
                    found = search_set(mode, detail);
                    if (!found) {
                        continue;
                    } else break;
                } else break;
            }
        }
        while (!found){
            if(memory_extend.get(origin) != null){
                found = check_ExtendMethod(memory_extend.get(origin), mode);
            } else break;
        }
        //found = check_import(origin, false);
        return found;
    }

    private static class FirstVisitor extends VoidVisitorAdapter<Void>{
        ArrayList<ImportDeclaration> Import_list = new ArrayList<>();

        @Override
        public void visit(ImportDeclaration md, Void arg){
            super.visit(md, arg);
            Import_list.add(md);
            
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration md, Void arg){
            memory_import.put(md.getNameAsString(), Import_list);
            SomeVisitor visitor = new SomeVisitor(md.getNameAsString());
            md.accept(visitor, null);
        }

    }

    private static class SomeVisitor extends VoidVisitorAdapter<Void> {
        String classname = "";
        List<MethodDeclaration> methodDeclarations = new ArrayList<>();
        List<FieldDeclaration> fieldDeclarations = new ArrayList<>();
        ArrayList<String> inner_list = new ArrayList<>();

        public SomeVisitor(String name){
            classname = name;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration md, Void arg){
            if(classname.equals(md.getNameAsString())){
                memory_classname.add(classname);
                System.out.println(classname);
                int size_extend = md.getExtendedTypes().size();
                int size_implement = md.getImplementedTypes().size();
                if(size_extend != 0){
                    memory_extend.put(classname, md.getExtendedTypes().get(0).getNameAsString());
                }
                if(size_implement != 0){
                    ArrayList<String> names = new ArrayList<>();
                    for(int i = 0; i < size_implement ;i++){
                        names.add(md.getImplementedTypes(i).getNameAsString());
                    }
                    memory_implement.put(classname, names);
                }

                fieldDeclarations = md.getFields();
                memory_classfield.put(classname, fieldDeclarations);
                methodDeclarations = md.getMethods();
                memory_classmethod.put(classname, methodDeclarations);
                memory_constructor.put(classname, md.getConstructors());

                super.visit(md, arg);
            } else {
                if(memory_innerclass.get(classname) == null) inner_list = new ArrayList<>();
                else inner_list = memory_innerclass.get(classname);
                inner_list.add(md.getNameAsString());
                memory_innerclass.put(classname, inner_list);
                SomeVisitor visitor = new SomeVisitor(md.getNameAsString());
                md.accept(visitor, null);
            }
        }
    }

    private static class Checking_Case3 extends VoidVisitorAdapter<Void>{
        String methodname = "";

        public Checking_Case3(String name){
            methodname = name;
        }

        @Override
        public void visit(MethodCallExpr md, Void arg){
            String calling = md.getNameAsString();
            if(calling.equals(methodname)){
                System.out.println(methodname + " is private method. " +
                        "This code can cause an error after converting to Kotlin.\n");
            }
        }

    }

    //継承先の呼び出し箇所→メソッドの存在場所の順(これまでと反対)に探す新しいバージョン
    private static class Checking_Case5and7 extends VoidVisitorAdapter<Void> {
        ArrayList<MethodCallExpr> list = new ArrayList<>();
        String classname = "";
        String methodname = "";
        String looking_argument = "";
        String mode = "";
        public Checking_Case5and7(String classname,String methodname, String looking_argument, String mode){
            this.classname = classname;
            this.methodname = methodname;
            this.looking_argument = looking_argument;
            this.mode = mode;
        }

        @Override
        public void visit(MethodCallExpr md, Void arg){

            boolean flag = false;
            if(mode.equals("case5"))flag = md.getScope().isEmpty() ;
            else if(mode.equals("case7") && !md.getScope().isEmpty())
                flag = md.getScope().get().isThisExpr();
            if(flag) {
                String methodname = md.getNameAsString();

                if (methodname.matches("get[A-Z].*")) {
                    if (md.getArguments() == null) {
                        boolean break_flag = false;
                        for(MethodDeclaration mine:memory_classmethod.get(classname)) {
                            String mine_name = mine.getNameAsString();
                            String mine_type = mine.getTypeAsString();
                            int mine_sizeParameter = mine.getParameters().size();
                            if (mine_name.equals(methodname)) {//自分のメソッドの場合
                                break_flag = true;
                                break;
                            }
                        }//継承元クラス・インターフェースのデフォルトクラスの場合

                        boolean warning_flag = false;
                        //implementsの確認、再帰関数使用
                        if (memory_implement.get(classname) != null) {
                            for (String name_interface : memory_implement.get(classname)) {
                                warning_flag = check_ImplementMethod(name_interface, mode);
                            }
                        }

                        //extendsの確認、再帰関数使用
                        if (memory_extend.get(classname) != null && !warning_flag) {
                            warning_flag = check_ExtendMethod(memory_extend.get(classname), mode);
                        }
                        if(!break_flag || warning_flag)  {
                                System.out.println("line " + md.getRange().get().begin.line);
                                System.out.println("This code may give the following error after converting to Kotlin: Val cannnot be reassigned.");
                                System.out.println("It is recommended to rename the argument \"" + looking_argument + "\" in the method \"" + this.methodname + "\".\n");
                        }
                    }
                } else if (methodname.matches("set[A-Z].*")) {
                    int size_param = md.getArguments().size();
                    if (size_param == 1) {
                        String argument = md.getArgument(0).toString();
                        String cut_field = methodname.split("set")[1].toLowerCase();
                        if (argument.equals(looking_argument) && cut_field.equals(argument)) {
                            boolean break_flag = false;
                            for(MethodDeclaration mine:memory_classmethod.get(classname)) {
                                String mine_name = mine.getNameAsString();
                                String mine_type = mine.getTypeAsString();
                                int mine_sizeParameter = mine.getParameters().size();
                                if (mine_name.equals(methodname)) {//自分のメソッドの場合
                                    if(!mine_type.equals("void")){//返り値voidかつ引数１
                                        if(mine_sizeParameter == 1){
                                            break_flag = true;
                                            break;
                                        }
                                    }
                                }
                            }//継承元クラス・インターフェースのデフォルトクラスの場合

                            boolean warning_flag = false;
                            //implementsの確認、再帰関数使用
                            if (memory_implement.get(classname) != null) {
                                for (String name_interface : memory_implement.get(classname)) {
                                    warning_flag = check_ImplementMethod(name_interface, mode);
                                }
                            }
                            //extendsの確認、再帰関数使用
                            if (memory_extend.get(classname) != null && !warning_flag) {
                                warning_flag = check_ExtendMethod(memory_extend.get(classname), mode);
                            }
                            if(!break_flag || warning_flag) {

                                    System.out.println("line " + md.getRange().get().begin.line);
                                    System.out.println("This code may give the following error after converting to Kotlin: Val cannnot be reassigned.");
                                    System.out.println("It is recommended to rename the argument \"" + looking_argument + "\" in the method \"" + this.methodname + "\".\n");
                            }
                        }
                    }
                }
            }
        }
    }

    public static void check_initialize(String classname){
            List<FieldDeclaration> field_list = memory_classfield.get(classname);
            if(field_list != null) {
                System.out.println("start field checking\n");
                for (FieldDeclaration field : field_list) {
                    int size = field.getVariables().size();
                    for (int i = 0; i < size; i++) {
                        if (field.getVariable(i).getInitializer().isEmpty()) {
                            boolean flag = true;
                            List<ConstructorDeclaration> CdList = memory_constructor.get(classname);
                            if(CdList != null) {
                                for (ConstructorDeclaration constructorDeclaration : CdList) {
                                    check_constructor visitor = new check_constructor(field.getVariable(i).getNameAsString());
                                    if(constructorDeclaration.accept(visitor, null) != null)
                                        flag = constructorDeclaration.accept(visitor, null);
                                }
                            }
                            if(flag){
                            System.out.println("line " + field.getRange().get().begin.line);
                            System.out.println("Field \"" + field.getVariable(i).getNameAsString()
                                    + "\" doesn't have initializer.");
                            if (!field.getVariable(i).getType().isPrimitiveType())
                                System.out.println("You should use modifer \"lateinit\" after convert to Kotlin");
                            System.out.println();
                            }
                        }
                    }
                }
                System.out.println("finished field checking\n");
            }
    }

    public static class check_constructor extends GenericVisitorAdapter<Boolean,Void> {
        String fieldname = "";

        public check_constructor(String fieldname) {
            this.fieldname = fieldname;
        }

        @Override
        public Boolean visit(AssignExpr md, Void arg){
            if(md.getTarget().getChildNodes().get(0).toString().equals("this")){
                if(md.getTarget().getChildNodes().get(1).toString().equals(fieldname)){
                    return false;
                }
            } else if(md.getTarget().toString().equals(fieldname)){
                return false;
            }
            return null;
        }
    }

    public static boolean check_import(String checkname, boolean already){
        for(String classname:memory_classname){
            if(classname.equals(checkname)){
                return false;
            }
        }
        if(!already)System.out.println("\""+checkname+"\" is probably a library. Please check the detail of the library if necessary.\n");
        return true;
    }

    public static void fullcheck_import(String checkname){
        boolean flag = true;
        if(memory_implement.get(checkname) != null) {
            for (String implement : memory_implement.get(checkname)) {
                flag = true;
                for (String classname : memory_classname) {
                    if (classname.equals(implement)) {
                        flag = false;
                        break;
                    }
                }
                if (flag && !duplicate_check(implement)) memory_classlibrary.add(implement);
            }
        }
        flag = true;
        String extend = memory_extend.get(checkname);
        for(String classname:memory_classname){
            if(classname.equals(extend)) {
                flag = false;
            }
        }
        if(extend == null) flag = false;
        if(flag && !duplicate_check(extend)) memory_classlibrary.add(extend);
    }

    public static boolean duplicate_check(String name){
        if(memory_classlibrary != null) {
            for (String library : memory_classlibrary) {
                if(library == null) break;
                if (library.equals(name)) return true;
            }
        }
        return false;
    }

}
