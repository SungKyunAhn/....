package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

public class VIF implements java.io.Serializable{
	private static Log log = LogFactory.getLog(VIF.class);
	private byte[] rawData = null;
	private int extensionBit=0;
	private String description="";
	private double multiplier=1;
	private String unit="";

	/**
	 * @param data
	 */
	public VIF(byte[] data) {
		intVIF();
		int value=0;
		rawData=data;
		int vif=DataUtil.getIntToBytes(rawData);
		extensionBit=((vif&0x80)>>7);
		//log.debug("EXTENSIONBIT[" + extensionBit + "]");

		value=(vif&0x7F);

		int multiValue=(vif&0x07);
		if(vif==0x0f){
			description="Energy";
			multiplier=1;
			unit="GJ";
		}
		else if(vif==0x0e){
			description="Energy";
			multiplier=1;
			unit="GJ";
		}
		else if(vif==0x05){
			description="Energy";
			multiplier=1;
			unit="kWh";
		}
		else if(vif==0x06){
			description="Energy";
			multiplier=1;
			unit="kWh";
		}
		else if(vif==0x07){
			description="Energy";
			multiplier=1;
			unit="MWh";
		}
		else if(vif==0x17){
			description="Volume";
			multiplier=10;
			unit="m3";
		}
		else if(vif==0x16){
			description="Water";
			multiplier=1;
			unit="m3";
		}
		else if(vif==0x15){
			description="Water";
			multiplier=0.1;
			unit="m3";
		}
		else if(vif==0x14){
			description="Water";
			multiplier=0.01;
			unit="m3";
		}
		else if(vif==0x13){
			description="Volume";
			multiplier=0.001;
			unit="m3";
		}
		else if(vif==0x22){
			description="Hour Counter";
			multiplier=Math.pow(10, multiValue-3);
			unit="hours";
		}
		else if(vif==0x3e){
			description="Flow";
			multiplier=1;
			unit="m3/h";
		}
		else if(vif==0x3d){
			description="Flow";
			multiplier=0.1;
			unit="m3/h";
		}
		else if(vif==0x3c){
			description="Flow";
			multiplier=0.01;
			unit="m3/h";
		}
		else if(vif==0x3b){
			description="Flow";
			multiplier=1;
			unit="l/h";
		}
		else if(vif==0x59){
			description="Temp. forward";
			multiplier=0.01;
			unit="C";
		}
		else if(vif==0x5d){
			description="Temp. return";
			multiplier=0.01;
			unit="C";
		}
		else if(vif==0x61){
			description="F-R Temp.";
			multiplier=0.01;
			unit="K";
		}
		else if(vif==0x2d){
			description="Power";
			multiplier=0.1;
			unit="kW";
		}
		else if(vif==0x2e){
			description="Power";
			multiplier=0.001;
			unit="MW";
		}
		else if(vif==0x2f){
			description="Power";
			multiplier=0.01;
			unit="Power";
		}
		else if(vif==0x0f){
			description="Energy";
			multiplier=Math.pow(10, multiValue-3);
			unit="Wh";
		}
		//Not Kamstrup Case
		else{
			if(value>=0x00 &&value<=0x07){
				description="Energy";
				multiplier=Math.pow(10, multiValue-3);
				unit="Wh";
			}
			else if(value>=0x08 &&value<=0x0F){
				description="Energy";
				multiplier=Math.pow(10, multiValue);
				unit="J";
			}
			else if(value>=0x10 &&value<=0x17){
				description="Volume";
				multiplier=Math.pow(10, multiValue-6);
				unit="m3";
			}
			else if(value>=0x18 &&value<=0x1F){
				description="Mass";
				multiplier=Math.pow(10, multiValue-3);
				unit="kg";
			}
			else if(value>=0x20 &&value<=0x23){
				multiValue=(vif&0x03);
				description="On Time";
				if(multiValue==0){
					unit="seconds";
				}
				else if(multiValue==1){
					unit="minutes";
				}
				else if(multiValue==2){
					unit="hours";
				}
				else if(multiValue==3){
					unit="days";
				}
			}
			else if(value>=0x24 &&value<=0x27){
				multiValue=(vif&0x03);
				description="Operating Time";
				if(multiValue==0){
					unit="seconds";
				}
				else if(multiValue==1){
					unit="minutes";
				}
				else if(multiValue==2){
					unit="hours";
				}
				else if(multiValue==3){
					unit="days";
				}
			}

			else if(value>=0x28 &&value<=0x2F){
				description="Power";
				multiplier=Math.pow(10, multiValue-3);
				unit="W";
			}
			else if(value>=0x30 &&value<=0x37){
				description="Power";
				multiplier=Math.pow(10, multiValue);
				unit="J/h";
			}
			else if(value>=0x38 &&value<=0x3F){
				description="Volume Flow";
				multiplier=Math.pow(10, multiValue-6);
				unit="m3/h";
			}
			else if(value>=0x40 &&value<=0x47){
				description="Volume Flow ext.";
				multiplier=Math.pow(10, multiValue-7);
				unit="m3/min";
			}
			else if(value>=0x48 &&value<=0x4F){
				description="Volume Flow ext.";
				multiplier=Math.pow(10, multiValue-9);
				unit="m3/s";
			}
			else if(value>=0x50 &&value<=0x57){

				description="Mass flow";
				multiplier=Math.pow(10, multiValue-3);
				unit="kg/h";
			}
			else if(value>=0x58 &&value<=0x5B){
				multiValue=(vif&0x03);
				description="Flow Temperature";
				multiplier=Math.pow(10, multiValue-3);
				unit="C";
			}
			else if(value>=0x5C &&value<=0x5F){
				multiValue=(vif&0x03);
				description="Return Temperature";
				multiplier=Math.pow(10, multiValue-3);
				unit="C";
			}
			else if(value>=0x60 &&value<=0x63){
				multiValue=(vif&0x03);
				description="Temperature Difference";
				multiplier=Math.pow(10, multiValue-3);
				unit="K";
			}
			else if(value>=0x64 &&value<=0x67){
				multiValue=(vif&0x03);
				description="External Temperature";
				multiplier=Math.pow(10, multiValue-3);
				unit="C";
			}
			else if(value>=0x68 &&value<=0x6B){
				multiValue=(vif&0x03);
				description="Pressure";
				multiplier=Math.pow(10, multiValue-3);
				unit="bar";
			}
			else if(value>=0x6C &&value<=0x6D){
				multiValue=(vif&0x01);
				description="Time Point";
				if(multiValue==0){
					unit="date";
				}else if(multiValue==1){
					unit="time & date";
				}
			}
			else if(value==0x6E){
				description="Units for H.C.A.";
			}
			else if(value==0x6F){
				description="Reserved";
			}
			else if(value>=0x70 &&value<=0x73){
				multiValue=(vif&0x03);
				description="Averaging Duration";
				if(multiValue==0){
					unit="seconds";
				}
				else if(multiValue==1){
					unit="minutes";
				}
				else if(multiValue==2){
					unit="hours";
				}
				else if(multiValue==3){
					unit="days";
				}
			}
			else if(value>=0x74 &&value<=0x77){
				multiValue=(vif&0x03);
				description="Actuality Duration";
				if(multiValue==0){
					unit="seconds";
				}
				else if(multiValue==1){
					unit="minutes";
				}
				else if(multiValue==2){
					unit="hours";
				}
				else if(multiValue==3){
					unit="days";
				}
			}
			else if(value==0x78){
				description="Fabrication No";
			}
			else if(value==0x79){
				description="Identification";
			}
			else if(value==0x7A){
				description="Bus Address";
			}
		}
		//log.debug("DESCRIPTION[" + description + "] MULTIPLIER[" + multiplier + "] UNIT[" + unit + "]");
	}

	public int getExtensionBit() {
		return extensionBit;
	}

	public void setExtensionBit(int extensionBit) {
		this.extensionBit = extensionBit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * Initialize VIF
	 */
	public void intVIF(){
		setExtensionBit(0);
		setDescription("");
		setMultiplier(0);
		setUnit("");
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("VIF ( ")
	        .append(super.toString()).append(TAB)
	        .append("rawData = ").append(this.rawData).append(TAB)
	        .append("extensionBit = ").append(this.extensionBit).append(TAB)
	        .append("description = ").append(this.description).append(TAB)
	        .append("multiplier = ").append(this.multiplier).append(TAB)
	        .append("unit = ").append(this.unit).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
