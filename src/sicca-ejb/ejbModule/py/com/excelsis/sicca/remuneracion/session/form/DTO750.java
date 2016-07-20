package py.com.excelsis.sicca.remuneracion.session.form;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;

public class DTO750 {
	public Integer aniAniopre;
	public Integer nenCodigo;
	public Integer entCodigo;
	public Integer tipCodigo;
	public Integer proCodigo;
	public Integer subCodigo;
	public Integer pryCodigo;
	public Integer objCodigo;
	public Integer fueCodigo;
	public Integer finCodigo;
	public Integer vrsCodigo;
	public Integer anxLinea;
	public Integer catGrupo;
	public String ctgCodigo;
	public String mdfCodigo;
	public String anxTiporeg;
	public Integer anxMesaplic;
	public Integer anxDurac;
	public Long anxVlran;
	public Integer anxCargos;
	public String anxDescrip;
	public String anxJustifica;
	public Integer paiCodigo;
	public Integer dptCodigo;

	public static DTO750 descomponerLinea(String unaLinea) {
		String[] compos = unaLinea.split(";");
		if (compos.length < 21) {
			return null;
		}
		try {
			DTO750 dto = new DTO750();
			if (compos[0] != null && !compos[0].trim().isEmpty())
				dto.setAniAniopre(Integer.parseInt(compos[0]));
			if (compos[1] != null && !compos[1].trim().isEmpty())
				dto.setNenCodigo(Integer.parseInt(compos[1]));
			if (compos[2] != null && !compos[2].trim().isEmpty())
				dto.setEntCodigo(Integer.parseInt(compos[2]));
			if (compos[3] != null && !compos[3].trim().isEmpty())
				dto.setTipCodigo(Integer.parseInt(compos[3]));
			if (compos[4] != null && !compos[4].trim().isEmpty())
				dto.setProCodigo(Integer.parseInt(compos[4]));
			if (compos[5] != null && !compos[5].trim().isEmpty())
				dto.setSubCodigo(Integer.parseInt(compos[5]));
			if (compos[6] != null && !compos[6].trim().isEmpty())
				dto.setPryCodigo(Integer.parseInt(compos[6]));
			if (compos[7] != null && !compos[7].trim().isEmpty())
				dto.setObjCodigo(Integer.parseInt(compos[7]));
			if (compos[8] != null && !compos[8].trim().isEmpty())
				dto.setFueCodigo(Integer.parseInt(compos[8]));
			if (compos[9] != null && !compos[9].trim().isEmpty())
				dto.setFinCodigo(Integer.parseInt(compos[9]));
			if (compos[10] != null && !compos[10].trim().isEmpty())
				dto.setVrsCodigo(Integer.parseInt(compos[10]));
			if (compos[11] != null && !compos[11].trim().isEmpty())
				dto.setAnxLinea(Integer.parseInt(compos[11]));
			if (compos[12] != null && !compos[12].trim().isEmpty())
				dto.setCatGrupo(Integer.parseInt(compos[12]));
			if (compos[13] != null && !compos[13].trim().isEmpty())
				dto.setCtgCodigo(compos[13]);
			if (compos[14] != null && !compos[14].trim().isEmpty())
				dto.setMdfCodigo(compos[14]);
			if (compos[15] != null && !compos[15].trim().isEmpty())
				dto.setAnxTiporeg(compos[15]);
			if (compos[16] != null && !compos[16].trim().isEmpty())
				dto.setAnxMesaplic(Integer.parseInt(compos[16]));
			if (compos[17] != null && !compos[17].trim().isEmpty())
				dto.setAnxDurac(Integer.parseInt(compos[17]));
			if (compos[18] != null && !compos[18].trim().isEmpty()) { 
				System.out.println(compos[18]);
				DecimalFormat df = new DecimalFormat();
				DecimalFormatSymbols dfs =DecimalFormatSymbols.getInstance(); 
				dfs.setDecimalSeparator((",".charAt(0)));
				df.setDecimalFormatSymbols(dfs);
				Number num = df.parse(compos[18].trim(), new ParsePosition(0));
				
				dto.setAnxVlran(num.longValue());
			}

			if (compos[19] != null && !compos[19].trim().isEmpty())
				dto.setAnxCargos(Integer.parseInt(compos[19]));

			if (compos[20] != null && !compos[20].trim().isEmpty())
				dto.setAnxDescrip(compos[20]);
			if (compos[21] != null && !compos[21].trim().isEmpty())
				dto.setAnxJustifica(compos[21]); 

			if (compos[22] != null && !compos[22].trim().isEmpty()) {
				System.out.println(" el: " + compos[22]);
				if (!compos[22].matches("[0-9]+")) {
					dto.setPaiCodigo(0);
				} else
					dto.setPaiCodigo(Integer.parseInt(compos[22]));
			}
			if (compos[23] != null && !compos[23].trim().isEmpty())
				dto.setDptCodigo(Integer.parseInt(compos[23]));
			return dto;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Integer getAniAniopre() {
		return aniAniopre;
	}

	public void setAniAniopre(Integer aniAniopre) {
		this.aniAniopre = aniAniopre;
	}

	public Integer getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(Integer nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	public Integer getEntCodigo() {
		return entCodigo;
	}

	public void setEntCodigo(Integer entCodigo) {
		this.entCodigo = entCodigo;
	}

	public Integer getTipCodigo() {
		return tipCodigo;
	}

	public void setTipCodigo(Integer tipCodigo) {
		this.tipCodigo = tipCodigo;
	}

	public Integer getProCodigo() {
		return proCodigo;
	}

	public void setProCodigo(Integer proCodigo) {
		this.proCodigo = proCodigo;
	}

	public Integer getSubCodigo() {
		return subCodigo;
	}

	public void setSubCodigo(Integer subCodigo) {
		this.subCodigo = subCodigo;
	}

	public Integer getPryCodigo() {
		return pryCodigo;
	}

	public void setPryCodigo(Integer pryCodigo) {
		this.pryCodigo = pryCodigo;
	}

	public Integer getObjCodigo() {
		return objCodigo;
	}

	public void setObjCodigo(Integer objCodigo) {
		this.objCodigo = objCodigo;
	}

	public Integer getFueCodigo() {
		return fueCodigo;
	}

	public void setFueCodigo(Integer fueCodigo) {
		this.fueCodigo = fueCodigo;
	}

	public Integer getFinCodigo() {
		return finCodigo;
	}

	public void setFinCodigo(Integer finCodigo) {
		this.finCodigo = finCodigo;
	}

	public Integer getVrsCodigo() {
		return vrsCodigo;
	}

	public void setVrsCodigo(Integer vrsCodigo) {
		this.vrsCodigo = vrsCodigo;
	}

	public Integer getAnxLinea() {
		return anxLinea;
	}

	public void setAnxLinea(Integer anxLinea) {
		this.anxLinea = anxLinea;
	}

	public Integer getCatGrupo() {
		return catGrupo;
	}

	public void setCatGrupo(Integer catGrupo) {
		this.catGrupo = catGrupo;
	}

	public String getCtgCodigo() {
		return ctgCodigo;
	}

	public void setCtgCodigo(String ctgCodigo) {
		this.ctgCodigo = ctgCodigo;
	}

	public String getMdfCodigo() {
		return mdfCodigo;
	}

	public void setMdfCodigo(String mdfCodigo) {
		this.mdfCodigo = mdfCodigo;
	}

	public String getAnxTiporeg() {
		return anxTiporeg;
	}

	public void setAnxTiporeg(String anxTiporeg) {
		this.anxTiporeg = anxTiporeg;
	}

	public Integer getAnxMesaplic() {
		return anxMesaplic;
	}

	public void setAnxMesaplic(Integer anxMesaplic) {
		this.anxMesaplic = anxMesaplic;
	}

	public Integer getAnxDurac() {
		return anxDurac;
	}

	public void setAnxDurac(Integer anxDurac) {
		this.anxDurac = anxDurac;
	}

	public Long getAnxVlran() {
		return anxVlran;
	}

	public void setAnxVlran(Long anxVlran) {
		this.anxVlran = anxVlran;
	}

	public Integer getAnxCargos() {
		return anxCargos;
	}

	public void setAnxCargos(Integer anxCargos) {
		this.anxCargos = anxCargos;
	}

	public String getAnxDescrip() {
		return anxDescrip;
	}

	public void setAnxDescrip(String anxDescrip) {
		this.anxDescrip = anxDescrip;
	}

	public String getAnxJustifica() {
		return anxJustifica;
	}

	public void setAnxJustifica(String anxJustifica) {
		this.anxJustifica = anxJustifica;
	}

	public Integer getPaiCodigo() {
		return paiCodigo;
	}

	public void setPaiCodigo(Integer paiCodigo) {
		this.paiCodigo = paiCodigo;
	}

	public Integer getDptCodigo() {
		return dptCodigo;
	}

	public void setDptCodigo(Integer dptCodigo) {
		this.dptCodigo = dptCodigo;
	}

}
