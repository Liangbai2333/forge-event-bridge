package site.liangbai.forgeeventbridge.asm.classcreator;

public interface IClassCreator {
    Class<?> create(String name, byte[] classBuffer);
}
