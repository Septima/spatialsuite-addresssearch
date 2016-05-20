CREATE TABLE sv_adr.addressaccess
(
  addressaccessid character varying NOT NULL,
  streetname character varying,
  streetbuildingidentifier character varying,
  postcodeidentifier character varying,
  districtname character varying,
  presentationstring character varying,
  geometrywkt character varying,
  sortorder int,
  CONSTRAINT addressaccess_pkey PRIMARY KEY (addressaccessid)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sv_adr.addressaccess
  OWNER TO postgres;

-- Index: sv_adr."addressaccess_streetname_postcodeIdentifier_streetbuilding_idx"

-- DROP INDEX sv_adr."addressaccess_streetname_postcodeIdentifier_streetbuilding_idx";

CREATE INDEX "addressaccess_streetname_postcodeIdentifier_sortorder_idx"
  ON sv_adr.addressaccess
  USING btree
  (streetname, postcodeidentifier, sortorder);

-- Index: sv_adr.addressaccess_streetname_streetbuildingidentifier_postcodei_idx

-- DROP INDEX sv_adr.addressaccess_streetname_streetbuildingidentifier_postcodei_idx;

CREATE INDEX addressaccess_streetname_postcodei_streetbuildingidentifier_idx
  ON sv_adr.addressaccess
  USING btree
  (streetname, postcodeidentifier, streetbuildingidentifier);

CREATE TABLE sv_adr.streetname
(
  id character varying NOT NULL,
  streetname character varying,
  postcodeidentifier character varying,
  districtname character varying,
  presentationstring character varying,
  CONSTRAINT streetname_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sv_adr.streetname
  OWNER TO postgres;

-- Index: sv_adr.streetname_streetname_postcodeidentifier_idx

-- DROP INDEX sv_adr.streetname_streetname_postcodeidentifier_idx;

CREATE INDEX streetname_streetname_postcodeidentifier_idx
  ON sv_adr.streetname
  USING btree
  (streetname, postcodeidentifier);

  
  insert	into sv_adr.addressaccess
select	adressplats_id as addressaccessid,
	adromrade_namn as streetname,
	nr_num || nr_litt as streetbuildingidentifier,
	postnr as postcodeidentifier,
	postort as districtname,
	adress_postnr_ort as presentationstring,
	wkt as geometrywkt,
	row_number()over(order by nr_num, nr_litt) as sortorder
from	sv_adr.adr_se
where	postnr is not null AND postnr <> ''
order	by nr_num, nr_litt

insert	into sv_adr.streetname
select	max(addressaccessid) as id,
	streetname as streetname,
	postcodeidentifier as postcodeidentifier,
	districtname as districtname,
	streetname || ' (' || postcodeidentifier || ' ' ||districtname || ')' as presentationstring
from	sv_adr.addressaccess
group	by streetname, postcodeidentifier, districtname, streetname || ' (' || postcodeidentifier || ' ' ||districtname || ')'