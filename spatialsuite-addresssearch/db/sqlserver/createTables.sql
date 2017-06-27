CREATE TABLE addressaccess
(
  addressaccessid nvarchar(100) NOT NULL,
  streetname nvarchar(100),
  streetbuildingidentifier nvarchar(3),
  postcodeidentifier nvarchar(10),
  districtname nvarchar(50),
  presentationstring nvarchar(MAX),
  geometrywkt nvarchar(MAX),
  sortorder integer,
  json nvarchar(MAX),
  CONSTRAINT addressaccess_pkey PRIMARY KEY (addressaccessid)
);

CREATE INDEX addressaccess_streetname_postcodeIdentifier_sortorder_idx
  ON addressaccess (streetname, postcodeidentifier, sortorder);

CREATE INDEX addressaccess_streetname_postcodei_streetbuildingidentifier_idx
  ON addressaccess (streetname, postcodeidentifier, streetbuildingidentifier);

CREATE TABLE streetname
(
  id nvarchar(100) NOT NULL,
  streetname nvarchar(100),
  postcodeidentifier nvarchar(100),
  districtname nvarchar(MAX),
  presentationstring nvarchar(MAX),
  CONSTRAINT streetname_pkey PRIMARY KEY (id)
);

CREATE INDEX streetname_streetname_postcodeidentifier_idx
  ON streetname (streetname, postcodeidentifier);
