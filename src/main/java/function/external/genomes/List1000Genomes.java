package function.external.genomes;

import function.AnalysisBase;
import function.variant.base.Region;
import function.variant.base.RegionManager;
import function.variant.base.VariantManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;
import utils.CommonCommand;
import utils.DBManager;
import utils.ErrorManager;

/**
 *
 * @author nick
 */
public class List1000Genomes extends AnalysisBase {

    BufferedWriter bw1000Genomes = null;
    final String genomesFilePath = CommonCommand.outputPath + "1000_genomes.csv";

    @Override
    public void initOutput() {
        try {
            bw1000Genomes = new BufferedWriter(new FileWriter(genomesFilePath));
            bw1000Genomes.write(GenomesOutput.getHeader());
            bw1000Genomes.newLine();
        } catch (Exception ex) {
            ErrorManager.send(ex);
        }
    }

    @Override
    public void closeOutput() {
        try {
            bw1000Genomes.flush();
            bw1000Genomes.close();
        } catch (Exception ex) {
            ErrorManager.send(ex);
        }
    }

    @Override
    public void doAfterCloseOutput() {
    }

    @Override
    public void beforeProcessDatabaseData() {
    }

    @Override
    public void afterProcessDatabaseData() {
    }

    @Override
    public void processDatabaseData() throws Exception {
        for (int r = 0; r < RegionManager.getRegionSize(); r++) {

            for (String varType : VariantManager.VARIANT_TYPE) {

//                if (VariantManager.isVariantTypeValid(r, varType)) {
//
//                    boolean isIndel = varType.equals("indel");
//
//                    Region region = RegionManager.getRegion(r, varType);
//
//                    String sqlCode = GenomesManager.getSql4Maf(isIndel, region);
//
//                    ResultSet rset = DBManager.executeReadOnlyQuery(sqlCode);
//
//                    while (rset.next()) {
//                        GenomesOutput output = new GenomesOutput(isIndel, rset);
//
//                        if (VariantManager.isVariantIdIncluded(output.genomes.getVariantId())
//                                && output.isValid()) {
//                            bw1000Genomes.write(output.genomes.getVariantId() + ",");
//                            bw1000Genomes.write(output.toString());
//                            bw1000Genomes.newLine();
//                        }
//                    }
//
//                    rset.close();
//                }
            }
        }
    }

    @Override
    public String toString() {
        return "Start running list 1000 genomes function";
    }
}
