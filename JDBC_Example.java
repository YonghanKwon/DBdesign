import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JDBC_Example {
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		try {
			Connection conn=DriverManager.getConnection("jdbc:mysql://localhost:/youtube?useUnicode=true&useJDBCCompliantTimezoneShift=true&"
					+"useLegacyDetetimeCode=false&serverTimezon=UTC","root","kwonyh0315@@");
			int checker;
			String checker_st;
			String selectsql;
			Scanner sc = new Scanner(System.in);
			System.out.println("1.시청기록 검색");
			System.out.println("2.추천영상 검색");
			System.out.println("3. 종료");
			checker=sc.nextInt();
			while(checker!=3) {
				if(checker==1) {
					System.out.println("검색을 원하는 사용자의 ID를 입력하시오");
					checker_st=sc.next();
					selectsql="select * from watch_record where user_id="+checker_st;
					Statement stmt= conn.createStatement();
					ResultSet rset = stmt.executeQuery(selectsql);
					while(rset.next()) {
						System.out.println("Id: "+rset.getString(1)+'\t'+"video_urls: "+rset.getString(2)+'\t'+"time: "+rset.getDouble(3)+'\t'+"watched_time: "+rset.getDouble(4));
					}
				}else if(checker==2) {
					String query="truncate table recommended_video";
					Statement trunstmt=conn.createStatement();
					trunstmt.execute(query);
					int[] cate_check= {0,0,0,0};
					System.out.println("검색을 원하는 사용자의 ID를 입력하시오");
					checker_st=sc.next();
					String VU="select video_urls from watch_record where user_id="+checker_st;
					Statement stmt_t= conn.createStatement();
					ResultSet rset_temp = stmt_t.executeQuery(VU);
					while(rset_temp.next()) {
						String video_urls=rset_temp.getString(1);
						String temp="select category from video where video_urls="+"'"+video_urls+"'";
						Statement stemp=conn.createStatement();
						ResultSet rset_t=stemp.executeQuery(temp);
						while(rset_t.next()) {
							String cate=rset_t.getString(1);
							if(cate.equals("game"))
								cate_check[0]++;
							else if(cate.equals("cook"))
								cate_check[1]++;
							else if(cate.equals("music"))
								cate_check[2]++;
							else if(cate.equals("animal"))
								cate_check[3]++;
						}
					}
				int max_index=0;
				int max=cate_check[0];
				for(int i=0;i<4;i++)
				{
					if(max<cate_check[i])
					{
						max=cate_check[i];
						max_index=i;
					}
				}
				String max_category;
				if(max_index==0)
					max_category="game";
				else if(max_index==1)
					max_category="cook";
				else if(max_index==2)
					max_category="music";
				else
					max_category="animal";
				String selectSql="select video_urls from video where category="+"'"+max_category+"'";
				Statement stmt= conn.createStatement();
				ResultSet rset = stmt.executeQuery(selectSql);
				while(rset.next()) {
					String insertsql="insert into recommended_video values(?,?)";
					PreparedStatement pstmt = conn.prepareStatement(insertsql);
					pstmt.setString(1,checker_st);
					pstmt.setString(2,rset.getString(1));
					pstmt.executeUpdate();
				}
				String selectS="select * from recommended_video where user_id="+checker_st;
				Statement Stmt= conn.createStatement();
				ResultSet Rset = Stmt.executeQuery(selectS);
				while(Rset.next()) {
					System.out.println("User id: "+Rset.getString(1)+'\t'+"video_urls: "+Rset.getString(2));
				}
			}
			System.out.println("1.시청기록 검색");
			System.out.println("2.추천영상 검색");
			System.out.println("3. 종료");
			checker=sc.nextInt();
			}
			conn.close();		
		}
		catch(SQLException sqle) {
			System.out.println("QULException : "+sqle);
		}
	}
	
}
