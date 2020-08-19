package cc.xstack.crack.xmind;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;

public class Agent {
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new Agent.MethodEntryTransformer());
    }

    private static class MethodEntryTransformer implements ClassFileTransformer {

        private MethodEntryTransformer() {
        }

        public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            try {
                if (className != null && className.startsWith("net/xmind/verify/internal/LicenseVerifier")) {
                    ClassReader cr = new ClassReader(classfileBuffer);
                    ClassNode cn = new ClassNode();
                    cr.accept(cn, 0);
                    List methodNodes = cn.methods;

                    for (Object methodNode1 : methodNodes) {
                        MethodNode methodNode = (MethodNode) methodNode1;
                        if ("checkSignatureValid".equals(methodNode.name)) {
                            Type[] type = Type.getArgumentTypes(methodNode.desc);
                            Type returnType = Type.getReturnType(methodNode.desc);
                            if (type.length == 3 && returnType.toString().equals("Z")) {
                                InsnList insnList = methodNode.instructions;
                                insnList.clear();
                                insnList.add((new InsnNode(4)));
                                insnList.add((new InsnNode(172)));
                                methodNode.exceptions.clear();
                                methodNode.visitEnd();
                                ClassWriter cw = new ClassWriter(0);
                                cn.accept(cw);
                                System.out.println(className + " -> " + methodNode.name + " -> " + methodNode.desc);
                                return cw.toByteArray();
                            }
                        }
                    }
                }
            } catch (Throwable var15) {
                var15.printStackTrace();
            }

            return classfileBuffer;
        }

        // $FF: synthetic method
        MethodEntryTransformer(Object x0) {
            this();
        }
    }
}
