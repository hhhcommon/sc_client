package test.java.test.encrypt.util;

import java.util.*;
import java.util.Map.Entry;

public class SignUtil{
	/**
	 * 叮当验签秘钥
	 */
	public static final String WALLET="4qjtgM8Eo3Zd89Vn7z9X6A==";
	/**
	 * 通用api验签密钥
	 */
	public static final String OPEN_SALT="M3A2OrXBF3ZcOFx0oCnZQ";
	
	/**
	 * 加密解密用户id的秘钥
	 */
	private static final String USER_SALT="pVrG+nGlxU4amKi39E6bTA==";
	
	/**
	 * 通用api验签密钥
	 */
	public static final String OPEN_AND142_SALT="M3A2OrXBF3ZcOFx0oCnH5";
	
    /**
     * 根据id 获取加密后的字符串
     * @param id
     * @return
     */
	public static String getStrId(Long id){
		if(id!=null){
			return EncryptUtil.getInstance().AESencode(id+"", USER_SALT);
		}
		return null;
	}
	/**
	 * 加密字符串
	 * @author ducongcong
	 * @createDate 2016年1月8日
	 * @updateDate 
	 * @param str 
	 * @return
	 */
	public static String getEncode(String str){
		if(str!=null){
			return EncryptUtil.getInstance().AESencode(str, USER_SALT);
		}
		return null;
	}
	/**
	 * 解密字符串
	 * @author ducongcong
	 * @createDate 2016年1月8日
	 * @updateDate 
	 * @param userId
	 * @return
	 */
	public static String getDecode(String userId){
		return EncryptUtil.getInstance().AESdecode(userId, USER_SALT);
	}
	
	public static void main(String[] args) {
		System.out.println("========"+getDecode("7D57BAF16AF939516274D8E9E064CAB47A3E80AC24C72F40714E8783C56D5965"));
	}
	
	/**
	 * 参数加签
	 * @param salt 加签验签秘钥
	 * @param kv 以键值对组成的参数
	 *      <p>如：userId=xxx,mobile=156</p>
	 *      <p>输入时为 verifySign(sign,"userId","xxx","mobile","156")</p>
	 * @return 正常返回加签后的字符串
	 *      <p>kv不为双数,返回null</p>
	 */
	public static String sign(String salt,String ... kv){
		LinkedHashMap<String,String> paramMap = getParamLinkedMap(kv);
		if(paramMap!=null){
			return sign(paramMap, salt);
		}
		return null;
	}
	/**
	 * 验签功能
	 * @param salt 加签验签秘钥
	 * @param sign url 传递过来的 加密后的字符串
	 * @param kv 以键值对组成的参数
	 *      <p>如：userId=xxx,mobile=156</p>
	 *      <p>输入时为 verifySign(sign,"userId","xxx","mobile","156")</p>
	 * @return 
	 *     <p>kv不为双数,返回false</p>
	 *     <p>计算出的sign和传递过来的sign一致， 返回true</p>
	 *     <p>计算出的sign和传递过来的sign不一致，返回false</p>
	 */
	public static boolean verifySign(String salt,String sign,String ... kv){
		LinkedHashMap<String,String> paramMap = SignUtil.getParamLinkedMap(kv);
		if(paramMap!=null){
			String signVal = SignUtil.sign(paramMap, salt);
			if(signVal!=null&&signVal.equals(sign)){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	/**
	 * 加签
	 * @param paraMap 按约定顺序传入的参数和值
	 * @param secretKey 秘钥 
	 * @return
	 */
	public static String sign(LinkedHashMap<String, String> paraMap, String secretKey){
		if(paraMap==null||paraMap.size()<=0){
			return null;
		}
		StringBuffer strBuf=new StringBuffer();
		for(Entry<String,String> ent:paraMap.entrySet()){
			strBuf.append(ent.getKey()).append(":");
			if(ent.getValue() == null){
				strBuf.append("");
			}else{
				strBuf.append(ent.getValue());
			}
		}
		String str = EncryptUtil.getInstance().MD5(strBuf.toString().replaceAll("!", ""), secretKey);
		return StringUtils.getReplaceStr(str);
	}
	/**
	 * 加签
	 * Map按照字典排序 转成 xx=1&xxx=2&xxxx=3
	 * 然后+ &key=加密key
	 * md5 转大写
	 * @param map
	 * @param secretKey
	 * @return
	 */
	public static String signUpper(Map<String,String> map,String secretKey){
		ArrayList<String> list = new ArrayList<>();
		for(Entry<String,String> entry:map.entrySet()){
		    if(StringUtils.isNotNull(entry.getValue().toString())){
		        list.add(entry.getKey() + "=" + entry.getValue() + "&");
		    }
		}
		int size = list.size();
		String [] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < size; i ++) {
		    sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		result += "key=" + secretKey;
		result =MD5Util.MD5Encode(result).toUpperCase();
		return result;
	}
	/**
	 * 验签 
	 * @param paraMap 参数Map
	 * @param secretKey 秘钥
	 * @param sign  传递过来的加签值
	 * @return
	 */
	public static boolean verifySign(LinkedHashMap<String, String> paraMap, String secretKey,String sign){
		String signVal = sign(paraMap, secretKey);
		if(signVal!=null&&signVal.equals(sign)){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	/**
	 * 将参数封装到 LinkedHashMap中
	 * @param str key value,key value 的数组
	 * @return
	 */
	public static LinkedHashMap<String, String> getParamLinkedMap(String ...str){
		if(str!=null&&(str.length%2==0)){
			LinkedHashMap<String, String> linkedMap=new LinkedHashMap<String, String>();
			for(int i=0;i<str.length/2;i++){
				linkedMap.put(str[i*2], str[i*2+1]);
			}
			return linkedMap;
		}
		return null;
	} 
	
	/**
	* @desc: 去掉空值与签名参数sign后的新签名参数
	* @author:hbt
	* @param sArray 签名参数组
	* @return
	 */
	private static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 把所有参数按key排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @author:hbt
     * @param params 参数
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {
    	Map<String, String> newParms = paraFilter(params);
        List<String> keys = new ArrayList<String>(newParms.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = newParms.get(key);
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }
	/**
	* @desc: 获取排序后的签名字符串
	* @author:hbt
	* @param paraMap 要签名的参数
	* @param secretKey 密钥
	* @return
	 */
    public static String getSign(Map<String, String> paraMap, String secretKey){
    	String prestr = createLinkString(paraMap);
    	String str = EncryptUtil.getInstance().MD5(prestr.replaceAll("!", ""), secretKey);
    	str = (str == null ? "" : str);
		return StringUtils.getReplaceStr(str);
    }
    /**
    * @desc: 将参数自动排序后验证签名 
    * @author:hbt
    * @param params  URL参数
    * @param secretKey 密钥
    * @return
     */
    public static boolean veryfy(Map<String, String> params, String secretKey) {
    	//获取签名结果
		String sign = "";
		if (params != null && params.get("sign") != null) {
			sign = params.get("sign");
		}
    	String newSign = getSign(params, secretKey);
        //获得签名验证结果
    	return sign.equals(newSign);
    }
}