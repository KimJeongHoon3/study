explain
select 
i.*
from 
cf_panel p
join cf_subpanel s on 
	(p.E_PANELID=s.E_PANELID and p.STEPID=s.STEPID)
join cf_subpanel_item_value i on (s.SUBPANELID=i.SUBPANELID and s.STEPID=i.STEPID)
where
p.E_PANELID='panel13' and p.STEPID='stepid13'

/*
이게 정석이다!!
*/