# --- !Ups

CREATE TABLE alldata(
   NDB_No            VARCHAR(5)  NOT NULL PRIMARY KEY 
  ,Shrt_Desc         VARCHAR(60) NOT NULL
  ,Water_g           NUMERIC(5,2)
  ,Energ_Kcal        INTEGER  NOT NULL
  ,Protein_g         NUMERIC(5,2) NOT NULL
  ,Lipid_Tot_g       NUMERIC(5,2) NOT NULL
  ,Ash_g             NUMERIC(5,2)
  ,Carbohydrt_g      NUMERIC(5,2) NOT NULL
  ,Fiber_TD_g        NUMERIC(4,1)
  ,Sugar_Tot_g       NUMERIC(5,2)
  ,Calcium_mg        INTEGER 
  ,Iron_mg           NUMERIC(5,2)
  ,Magnesium_mg      INTEGER 
  ,Phosphorus_mg     INTEGER 
  ,Potassium_mg      INTEGER 
  ,Sodium_mg         INTEGER 
  ,Zinc_mg           NUMERIC(5,2)
  ,Copper_mg         NUMERIC(6,3)
  ,Manganese_mg      NUMERIC(6,3)
  ,Selenium_mu_g       NUMERIC(5,1)
  ,Vit_C_mg          NUMERIC(6,1)
  ,Thiamin_mg        NUMERIC(6,3)
  ,Riboflavin_mg     NUMERIC(5,3)
  ,Niacin_mg         NUMERIC(6,3)
  ,Panto_Acid_mg     NUMERIC(6,3)
  ,Vit_B6_mg         NUMERIC(5,3)
  ,Folate_Tot_mu_g     INTEGER 
  ,Folic_Acid_mu_g     INTEGER 
  ,Food_Folate_mu_g    INTEGER 
  ,Folate_DFE_mu_g     INTEGER 
  ,Choline_Tot_mg    NUMERIC(6,1)
  ,Vit_B12_mu_g        NUMERIC(5,2)
  ,Vit_A_IU          INTEGER 
  ,Vit_A_RAE         INTEGER 
  ,Retinol_mu_g        INTEGER 
  ,Alpha_Carot_mu_g    INTEGER 
  ,Beta_Carot_mu_g     INTEGER 
  ,Beta_Crypt_mu_g     INTEGER 
  ,Lycopene_mu_g       INTEGER 
  ,LutZea_mu_g         INTEGER 
  ,Vit_E_mg          NUMERIC(5,2)
  ,Vit_D_mu_g          NUMERIC(4,1)
  ,Vit_D_IU          INTEGER 
  ,Vit_K_mu_g          NUMERIC(6,1)
  ,FA_Sat_g          NUMERIC(6,3)
  ,FA_Mono_g         NUMERIC(6,3)
  ,FA_Poly_g         NUMERIC(6,3)
  ,Cholestrl_mg      INTEGER 
  ,GmWt_1            NUMERIC(6,2)
  ,GmWt_Desc1        VARCHAR(77)
  ,GmWt_2            NUMERIC(6,2)
  ,GmWt_Desc2        VARCHAR(80)
  ,Refuse_Pct        INTEGER 
);
# --- !Downs

DROP TABLE alldata;
