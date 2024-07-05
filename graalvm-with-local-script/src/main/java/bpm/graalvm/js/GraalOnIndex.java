package bpm.graalvm.js;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;


public class GraalOnIndex {
    private static final String JAVA_VERSION = System.getProperty("java.version");
    private static final String JAVA_VENDOR = System.getProperty("java.vendor");

    private static final String IMPORTS = "AddCommand, AddPaneToFormCommand, BomTypesRegistry, " +
            "CommandFactory, CommandManager, ComponentIns, ComponentTypeIns, " +
            "ComponentTypesRegistry, ComponentUtil, ControlIns, CurrentContextService, " +
            "CustomPropertyDescriptor, CustomPropertyDescriptorIns, CustomPropertyIns, " +
            "DeviceManager, ExternalResource, FormFactory, FormIns, FormPackageLiterals," +
            " FormUtil, GridAreaIns, GridColumnRowSizeIns, LOGGER, LogService, ModelTemplatesService," +
            " NamedElementIns, Notification, PaneIns, PaneUtil, PropertyChangeCommand, ServiceLoader," +
            " ServiceOperation, ServiceRegistry, StringUtil, TypeUtil, external ";

    private static final ExtractFileToTemp extractor = new ExtractFileToTemp();
    private static final String jarFilePath = "javascript-1.0-SNAPSHOT.jar";
    private static final String fileName = "index.mjs";
    private static String filePath;

    private Engine engine = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build();

    public static void main(String[] args) {
        System.out.println("Java " + JAVA_VENDOR + "-" + JAVA_VERSION);
        GraalOnIndex graalOnIndex = new GraalOnIndex();
        Long start = System.currentTimeMillis();
        filePath = convertWindowsToUnixPath(getFilePath());

        graalOnIndex.runContext1();
        graalOnIndex.runContext2();

        Long end = System.currentTimeMillis();
        System.out.println("Total time: " + (end - start) + " ms");
    }

    private static String getFilePath() {
        if (filePath == null) {
            filePath = extractor.getTempFilePath(jarFilePath, fileName);
        }
        return filePath;
    }

    private static String generateScript(String filePath) {
        return "import {" + IMPORTS + "} from '" + filePath + "';\n" +
                "const feature = 'exampleFeature';\n" +
                "const owner= 'owner';\n" +
                "const values = [1, 2, 3];    \n" +
                "const addCommand = new AddCommand(owner, feature, ...values);\n" +
                "console.log(addCommand.commandName);\n" +
                "const addPaneToFormCommand = new AddPaneToFormCommand();\n" +
                "console.log(addPaneToFormCommand.commandName);\n" +
                "const customPropertyDescriptorIns = new CustomPropertyDescriptorIns();\n" +
                "console.log(customPropertyDescriptorIns.hasDefaultValue);\n" +
                "const bomTypesRegistry = new BomTypesRegistry();\n" +
                "bomTypesRegistry.addClass(addCommand, { id: 'id123', attribute: 'values' });\n" +
                "console.log(JSON.stringify(bomTypesRegistry.allTypeNames, null, 2));";
    }

    public static String convertWindowsToUnixPath(String windowsPath) {
        String unixPath = windowsPath.replace("\\", "/");
        unixPath = unixPath.replaceAll("^[a-zA-Z]:", "");
        return unixPath;
    }

    public void runContext1() {
        try {
            String script = generateScript(filePath);
            Long start = System.currentTimeMillis();
            Context cx = Context.newBuilder("js")
                    .engine(engine)
                    .allowAllAccess(true)
                    .build();
            cx.eval(Source.newBuilder("js", script, "")
                    .mimeType("application/javascript+module")
                    .build());
            Long end = System.currentTimeMillis();
            System.out.println("Total time for context1: " + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runContext2() {
        try {
            String script = generateScript(filePath);
            Long start = System.currentTimeMillis();
            Context cx = Context.newBuilder("js")
                    .engine(engine)
                    .allowAllAccess(true)
                    .build();
            cx.eval(Source.newBuilder("js", script, "")
                    .mimeType("application/javascript+module")
                    .build());
            Long end = System.currentTimeMillis();
            System.out.println("Total time for context2: " + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
