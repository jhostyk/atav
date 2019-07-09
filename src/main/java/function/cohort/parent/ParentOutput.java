package function.cohort.parent;

import function.cohort.base.CalledVariant;
import function.cohort.base.Carrier;
import function.cohort.base.Sample;
import function.variant.base.Output;
import java.util.StringJoiner;

/**
 *
 * @author nick
 */
public class ParentOutput extends Output {

    // Family data
    Sample child;
    Sample mother;
    Sample father;
    Carrier cCarrier;
    Carrier mCarrier;
    Carrier fCarrier;

    public static String getTitle() {
        StringJoiner sj = new StringJoiner(",");

        sj.add("Family ID");
        sj.add("Parent");
        sj.add("Comp Het Flag");
        sj.merge(initVarTitleStr("1"));
        sj.merge(initVarTitleStr("2"));

        return sj.toString();
    }

    private static StringJoiner initVarTitleStr(String var) {
        String[] columnList = getTitleByVariant().split(",");
        StringJoiner sj = new StringJoiner(",");

        for (String column : columnList) {
            sj.add(column + " (#" + var + ")");
        }

        return sj;
    }

    private static String getTitleByVariant() {
        StringJoiner sj = new StringJoiner(",");

        sj.merge(Output.getVariantDataTitle());
        sj.merge(Output.getAnnotationDataTitle());

        sj.merge(initCarrierTitle("child"));
        sj.merge(initCarrierTitle("mother"));
        sj.merge(initCarrierTitle("father"));

        sj.merge(Output.getCohortLevelTitle());
        sj.merge(Output.getExternalDataTitle());

        return sj.toString();
    }

    private static StringJoiner initCarrierTitle(String str) {
        String[] columnList = Output.getCarrierDataTitle().toString().split(",");
        StringJoiner sj = new StringJoiner(",");

        for (String column : columnList) {
            sj.add(column + " (" + str + ")");
        }

        return sj;
    }

    public ParentOutput(CalledVariant c) {
        super(c);
    }

    public void initFamilyData(Family family) {
        child = family.getChild();
        cCarrier = calledVar.getCarrier(child.getId());

        mother = family.getMother();
        mCarrier = calledVar.getCarrier(mother.getId());

        father = family.getFather();
        fCarrier = calledVar.getCarrier(father.getId());
    }

    public StringJoiner getStringJoiner() {
        StringJoiner sj = new StringJoiner(",");

        calledVar.getVariantData(sj);
        calledVar.getAnnotationData(sj);

        getCarrierData(sj, cCarrier, child);
        getCarrierData(sj, mCarrier, mother);
        getCarrierData(sj, fCarrier, father);

        getGenoStatData(sj);
        calledVar.getExternalData(sj);

        return sj;
    }

    public byte getChildGT() {
        return calledVar.getGT(child.getIndex());
    }

    public byte getMotherGT() {
        return calledVar.getGT(mother.getIndex());
    }

    public byte getFatherGT() {
        return calledVar.getGT(father.getIndex());
    }

    @Override
    public String toString() {
        return getStringJoiner().toString();
    }
}