import {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import type {Group, GroupMember} from '@/types';
import {groupService} from '@/services';
import {useAuth} from '@/hooks/useAuth';
import {InviteMemberModal} from '@/components/InviteMemberModal';
import styles from './GroupDetailPage.module.css';

export function GroupDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [group, setGroup] = useState<Group | null>(null);
  const [members, setMembers] = useState<GroupMember[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();

  const isOwner = group?.ownerId === user?.id;

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      try {
        const [groupData, membersData] = await Promise.all([
          groupService.getById(Number(id)),
          groupService.getMembers(Number(id)),
        ]);
        setGroup(groupData);
        setMembers(membersData);
      } catch (error) {
        console.error('Failed to fetch group:', error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleMemberInvited = (newMember: GroupMember) => {
    setMembers([...members, newMember]);
    setShowInviteModal(false);
  };

  const handleDeleteGroup = async () => {
    if (!group) return;
    if (!window.confirm(`Are you sure you want to delete "${group.name}"? This cannot be undone.`)) return;
    setIsDeleting(true);
    try {
      await groupService.delete(group.id);
      navigate('/groups');
    } catch (error) {
      console.error('Failed to delete group:', error);
      setIsDeleting(false);
    }
  };

  if (isLoading) {
    return (
      <div className={styles.loading}>
        <span className={styles.loadingIcon}>&#x1F3B2;</span>
        <p>Loading group...</p>
      </div>
    );
  }

  if (!group) {
    return (
      <div className="container">
        <div className={styles.notFound}>
          <h2>Group not found</h2>
          <Link to="/groups" className="btn btn-primary">
            Back to Groups
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <Link to="/groups" className={styles.backLink}>
        &larr; Back to Groups
      </Link>

      <div className={styles.header}>
        <div className={styles.headerInfo}>
          <div className={styles.titleRow}>
            <span className={styles.icon}>&#x265F;</span>
            <h1>{group.name}</h1>
            {isOwner && <span className="badge badge-gold">Owner</span>}
          </div>
          <p className="text-muted">
            Created on {new Date(group.createdAt).toLocaleDateString()}
          </p>
        </div>
        {isOwner && (
          <div style={{ display: 'flex', gap: 'var(--spacing-sm)' }}>
            <button className="btn btn-primary" onClick={() => setShowInviteModal(true)}>
              + Invite Member
            </button>
            <button
              className="btn btn-danger"
              onClick={handleDeleteGroup}
              disabled={isDeleting}
            >
              {isDeleting ? 'Deleting...' : 'Delete Group'}
            </button>
          </div>
        )}
      </div>

      <div className={styles.section}>
        <h2>Members ({members.length})</h2>
        <div className={styles.memberList}>
          {members.map((member) => (
            <div key={member.id} className={styles.memberCard}>
              <div className={styles.avatar}>
                {member.nickname.charAt(0).toUpperCase()}
              </div>
              <div className={styles.memberInfo}>
                <span className={styles.memberName}>{member.nickname}</span>
                <span className={styles.memberTag}>{member.userTag}</span>
              </div>
              {member.id === group.ownerId && (
                <span className="badge badge-gold">Owner</span>
              )}
            </div>
          ))}
        </div>
      </div>

      {showInviteModal && (
        <InviteMemberModal
          groupId={group.id}
          existingMemberIds={members.map((m) => m.id)}
          onClose={() => setShowInviteModal(false)}
          onInvited={handleMemberInvited}
        />
      )}
    </div>
  );
}
