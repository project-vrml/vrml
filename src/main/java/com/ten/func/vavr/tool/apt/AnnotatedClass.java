//package com.ten.func.vavr.tool.apt;
//
//import javax.lang.model.element.TypeElement;
//import javax.tools.JavaFileObject;
//
//public class AnnotatedClass {
//    public TypeElement mClassElement;
//
//
//    public JavaFile generateFinder() {
//        // method inject(final T host, Object source, Provider provider)
//        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject")
//                .addModifiers(Modifier.PUBLIC)
//                .addAnnotation(Override.class)
//                .addParameter(TypeName.get(mClassElement.asType()), "host", Modifier.FINAL)
//                .addParameter(TypeName.OBJECT, "source")
//                .addParameter(TypeUtil.PROVIDER, "provider");
//        for (BindViewField field : mFields) {
//            // find views
//            injectMethodBuilder.addStatement("host.$N = ($T)(provider.findView(source, $L))", field.getFieldName(),
//                    ClassName.get(field.getFieldType()), field.getResId());
//        }
//        if (mMethods.size() > 0) {
//            injectMethodBuilder.addStatement("$T listener", TypeUtil.ANDROID_ON_CLICK_LISTENER);
//        }
//        for (OnClickMethod method : mMethods) {
//            // declare OnClickListener anonymous class
//            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
//                    .addSuperinterface(TypeUtil.ANDROID_ON_CLICK_LISTENER)
//                    .addMethod(MethodSpec.methodBuilder("onClick")
//                            .addAnnotation(Override.class)
//                            .addModifiers(Modifier.PUBLIC)
//                            .returns(TypeName.VOID)
//                            .addParameter(TypeUtil.ANDROID_VIEW, "view")
//                            .addStatement("host.$N()", method.getMethodName())
//                            .build())
//                    .build();
//            injectMethodBuilder.addStatement("listener = $L ", listener);
//            for (int id : method.ids) {
//                // set listeners
//                injectMethodBuilder.addStatement("provider.findView(source, $L).setOnClickListener(listener)", id);
//            }
//        }
//        // generate whole class
//        TypeSpec finderClass = TypeSpec.classBuilder(mClassElement.getSimpleName() + "$$Finder")
//                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.FINDER, TypeName.get(mClassElement.asType())))
//                .addMethod(injectMethodBuilder.build())
//                .build();
//        String packageName = mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();
//        // generate file
//        return JavaFile.builder(packageName, finderClass).build();
//    }
//}
