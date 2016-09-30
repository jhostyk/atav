package function.external.knownvar;

import function.variant.base.Variant;
import java.util.Collection;

/**
 *
 * @author nick
 */
public class DBDSMOutput {

    private Variant var;

    private DBDSM_test dbDSM;

    public DBDSMOutput(Variant var, Collection<DBDSM_test> collection) {
        this.var = var;

        dbDSM = getDBDSM(collection);
    }

    /*
     1. get DBDSM by matching chr-pos-ref-alt
     2. or return site accumulated DBDSM
     */
    private DBDSM_test getDBDSM(Collection<DBDSM_test> collection) {
        DBDSM_test dbDSM = new DBDSM_test(
                var.getChrStr(),
                var.getStartPosition(),
                var.getRefAllele(),
                var.getAllele(),
                "NA",
                "NA",
                "NA");

        boolean isFirstSite = true;

        for (DBDSM_test tmpDBDSM : collection) {
            String idStr = var.getVariantIdStr().replaceAll("XY", "X");

            if (idStr.equals(tmpDBDSM.getVariantId())) {
                return tmpDBDSM;
            }

            if (var.isIndel()) { // site values only for SNVs
                continue;
            }

            if (isFirstSite) {
                isFirstSite = false;
                dbDSM.setDiseaseName("?Site - " + tmpDBDSM.getDiseaseName());
                dbDSM.setClassification(tmpDBDSM.getClassification());
                dbDSM.setPubmedID(tmpDBDSM.getPubmedID());
            } else {
                dbDSM.append(
                        tmpDBDSM.getDiseaseName(),
                        tmpDBDSM.getClassification(),
                        tmpDBDSM.getPubmedID());
            }
        }

        return dbDSM;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(dbDSM.getDiseaseName()).append(",");
        sb.append(dbDSM.getClassification()).append(",");
        sb.append(dbDSM.getPubmedID()).append(",");

        return sb.toString();
    }
}
